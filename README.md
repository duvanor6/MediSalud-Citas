# 🏥 MediSalud - Enterprise Medical Appointment Engine

Sistema de agendamiento de citas médicas de alta criticidad, diseñado bajo los paradigmas de **Domain-Driven Design (DDD)** y **Arquitectura Hexagonal**. El núcleo del sistema ejecuta de forma determinista las reglas tácticas del negocio médico, garantizando la consistencia transaccional, control estricto de agendas concurrentes y persistencia desacoplada.

* **Producción URL:** `https://www.duban-curriculum.com`
* **Base Path API:** `/api/v1`

---

## Arquitectura de Despliegue en Producción

El sistema se ejecuta sobre una infraestructura moderna, optimizada para entornos cloud con recursos medidos de manera eficiente:

* **Infraestructura Cloud:** Desplegado sobre una instancia Compute Engine en la capa gratuita de **Google Cloud Platform (GCP)**.
* **Virtualización con Podman:** Ejecutado en un contenedor **Podman** de forma aislada, utilizando políticas estrictas de restricción de memoria en la JVM (`-Xmx350m -Xms256m`) para optimizar el recolector de basura.
* **Estrategia de Red (Network Host):** El contenedor comparte el espacio de nombres de red del host (`--network host`), anulando el overhead del puente virtual de red. Esto permite a la aplicación consumir la base de datos MySQL local en `localhost:3306` e interactuar de forma directa en el puerto `8082`.
* **Proxy Inverso y SSL:** Un servidor **Nginx** en el host intercepta el tráfico cifrado de `duban-curriculum.com`, gestiona el TLS/SSL (Certbot) y redirige de forma segura las peticiones del bloque `/api/` hacia el puerto interno `8082`.

---

## Justificación Táctica de la Arquitectura

* **Aislamiento Total del Core Dominio (`domain/`):** El modelo de negocio (`Appointment`, `Patient`, `Doctor`) carece de dependencias externas. Desconoce la existencia de Spring Boot, JPA, Hibernate o Jackson. Las Reglas de Negocio (RN) se auto-defienden mediante invariantes puros del modelo.
* **Inversión de Dependencias Mediante Puertos (`domain/repository/*Port`):** La persistencia en la infraestructura se ve obligada a cumplir los contratos definidos por el dominio. Esto permite mutar el motor de base de datos o el framework de persistencia sin alterar el comportamiento lógico del negocio.
* **Inmutabilidad Controlada:** El estado de las entidades solo muta mediante métodos de comportamiento explícitos (`appointment.cancel()`), eliminando el antipatrón de setters promiscuos que corrompen el estado lógico del software.

---

## Tecnologías Utilizadas

* **Java 17** (Uso extensivo de records, inmutabilidad y streams).
* **Spring Boot 3.x** (Motor de inyección de dependencias y exposición del módulo REST).
* **Spring Data JPA / Hibernate** (Capa de adaptación de persistencia).
* **Podman & Nginx** (Orquestación de infraestructura y proxy inverso seguro).
* **JUnit 5 & Mockito** (Suite automatizada de 15 pruebas tácticas con 100% de éxito funcional).

---

## API Endpoints (Catálogo Simplificado)

Todas las peticiones en producción deben anteponer el dominio seguro y el prefijo de versión: `https://www.duban-curriculum.com/api/v1`

### 🧑‍🦱 Gestión de Pacientes
* **`POST`** `/pacientes`  
  *Crea un paciente en el sistema cumpliendo con las validaciones de identidad obligatorias (RF-02).*

### 🩺 Gestión de Médicos
* **`POST`** `/doctores`  
  *Registra un médico con su respectiva especialidad dentro del sistema hospitalario (RF-01).*

### 📅 Motor de Citas Médicas
* **`POST`** `/citas`  
  *Permite a un paciente reservar un turno con un médico, validando franjas de 30 minutos, fines de semana, festivos, colisiones de agenda y edad opcional (RF-03 / RN-01 a RN-04).*
* **`GET`** `/citas/franjas-disponibles`  
  *Devuelve los bloques horarios libres de 30 minutos para un médico en un rango de fechas, deduciendo dinámicamente los turnos ya ocupados (RF-04).*
* **`PUT`** `/citas/:id/reprogramar`  
  *Modifica la fecha/hora de una cita existente, aplicando la cancelación de la anterior y la validación inmediata del nuevo cupo (RN-06).*
* **`DELETE`** `/citas/:id`  
  *Cancela una cita médica liberando el espacio e indexando penalizaciones automáticas en el perfil del paciente si se ejecuta con menos de 2 horas de anticipación (RF-05 / RN-05).*
* **`GET`** `/citas`  
  *Lista y audita las planificaciones del hospital aplicando filtros combinados y opcionales por médico, paciente, estado y rango temporal (RF-06).*

---

## Comandos de Infraestructura (Ejecución Local)

Si se desea clonar y ejecutar el entorno de simulación localmente:

### Clonar e iniciar la API
```bash
./mvnw spring-boot:run
