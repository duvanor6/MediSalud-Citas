package com.medisalud.citas.infrastructure.output.persistence.adapter;


import com.medisalud.citas.domain.model.Patient;
import com.medisalud.citas.domain.repository.PatientRepositoryPort;
import com.medisalud.citas.infrastructure.output.persistence.entity.PatientEntity;
import com.medisalud.citas.infrastructure.output.persistence.repository.PatientJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class PatientPersistenceAdapter implements PatientRepositoryPort {

    private final PatientJpaRepository repository;

    public PatientPersistenceAdapter(PatientJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Patient save(Patient patient) {
        PatientEntity entity = new PatientEntity();
        entity.setId(patient.getId());
        entity.setName(patient.getName());
        entity.setDocument(patient.getDocument());
        entity.setPhone(patient.getPhone());
        entity.setEmail(patient.getEmail());
        entity.setPenaltyDates(patient.getPenaltyDates() != null ? new ArrayList<>(patient.getPenaltyDates()) : new ArrayList<>());

        PatientEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByDocument(String document) {
        return repository.existsByDocument(document);
    }

    public Patient toDomain(PatientEntity entity) {
        return new Patient(entity.getId(), entity.getName(), entity.getDocument(), entity.getPhone(), entity.getEmail(), entity.getPenaltyDates());
    }
}