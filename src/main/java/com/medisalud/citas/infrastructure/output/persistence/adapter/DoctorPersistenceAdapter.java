package com.medisalud.citas.infrastructure.output.persistence.adapter;

import com.medisalud.citas.domain.model.Doctor;
import com.medisalud.citas.domain.repository.DoctorRepositoryPort;
import com.medisalud.citas.infrastructure.output.persistence.entity.DoctorEntity;
import com.medisalud.citas.infrastructure.output.persistence.repository.DoctorJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DoctorPersistenceAdapter implements DoctorRepositoryPort {

    private final DoctorJpaRepository repository;

    public DoctorPersistenceAdapter(DoctorJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Doctor save(Doctor doctor) {
        DoctorEntity entity = new DoctorEntity();
        entity.setId(doctor.getId());
        entity.setName(doctor.getName());
        entity.setSpecialty(doctor.getSpecialty());
        entity.setPhone(doctor.getPhone());
        entity.setEmail(doctor.getEmail());

        DoctorEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Doctor> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    // Utilidad de mapeo interno
    public Doctor toDomain(DoctorEntity entity) {
        return new Doctor(entity.getId(), entity.getName(), entity.getSpecialty(), entity.getPhone(), entity.getEmail());
    }
}