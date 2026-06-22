package com.medisalud.citas.domain.repository;

import com.medisalud.citas.domain.model.Doctor;

import java.util.Optional;

public interface DoctorRepositoryPort {

    Doctor save(Doctor doctor);

    Optional<Doctor> findById(Long id);
}