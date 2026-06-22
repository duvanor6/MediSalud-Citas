package com.medisalud.citas.domain.repository;

import com.medisalud.citas.domain.model.Patient;

import java.util.Optional;

public interface PatientRepositoryPort {

    Patient save(Patient patient);

    Optional<Patient> findById(Long id);

    // Método necesario para validar la regla implícita del RF-02 (Documento único)
    boolean existsByDocument(String document);
}