# 🏥 MediSalud - Sistema de Gestión de Citas Médicas

Este proyecto es una API REST de nivel empresarial diseñada para la gestión, reserva, reprogramación y cancelación automatizada de citas médicas. El sistema implementa un motor táctico que hace cumplir de manera estricta las reglas de negocio (RN) del centro de salud, garantizando consistencia, alta disponibilidad de horarios y control de penalizaciones.

---

## Justificación de la Arquitectura (Clean Architecture / Hexagonal)

Para este sistema se optó por una **Arquitectura Hexagonal (Puertos y Adaptadores)** combinada con **Domain-Driven Design (DDD)** de forma estricta. 

### ¿Por qué esta decisión?
1. **Aislamiento Total del Dominio:** El núcleo del negocio (`domain/model`) contiene la lógica pura de las citas, médicos y pacientes. No sabe nada de Spring Boot, bases de datos, redes ni JSON. Las reglas de negocio se defienden solas.
2. **Inversión de Dependencias:** La capa de aplicación y dominio define sus necesidades a través de interfaces llamadas Puertos (`domain/repository/*Port`). La infraestructura (JPA, Controladores REST) se ve obligada a adaptarse al Dominio, y no al revés.
3. **Mantenibilidad y Sustituibilidad:** Si mañana decidimos cambiar MySQL por MongoDB, o Spring Boot por Quarkus, el 100% de la lógica de agendamiento, cálculo de franjas y penalizaciones se mantiene intacta sin tocar una sola línea del core.

> **Nota de Diseño Táctico:** La entidad `Appointment` ha sido blindada como un objeto inmutable en su estado primario. Modificaciones críticas como la cancelación (`.cancel()`) exigen un comportamiento explícito y controlado por el modelo, impidiendo estados corruptos en la persistencia.

---

## Tecnologías Utilizadas

* **Java 17** (Aprovechamiento de inmutabilidad y estructuras modernas).
* **Spring Boot 3.x** (Framework de infraestructura para inyección de dependencias y capa REST).
* **Spring Data JPA / Hibernate** (Abstracción de persistencia).
* **MySQL 8.x** (Motor de base de datos relacional).
* **JUnit 5 & Mockito** (Suite de pruebas automatizadas para aislamiento de capas).
* **Maven** (Gestor de dependencias y automatización de construcción).

---

## Instrucciones de Ejecución Local

### Prerrequisitos
* Java 17 instalado.
* Instancia de MySQL corriendo con una base de datos llamada `medisalud`.
* Configurar las credenciales en `src/main/resources/application.yml`.

### 1. Levantar la Aplicación
Ejecuta el siguiente comando en la raíz del proyecto para descargar dependencias e iniciar el servidor embebido:

```bash
./mvnw spring-boot:run
