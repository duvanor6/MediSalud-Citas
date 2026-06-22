package com.medisalud.citas.infrastructure.output.persistence.repository;

import com.medisalud.citas.infrastructure.output.persistence.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, Long> {

    // Método necesario para validar la regla del RF-02 (Documento único)
    boolean existsByDocument(String document);
}
