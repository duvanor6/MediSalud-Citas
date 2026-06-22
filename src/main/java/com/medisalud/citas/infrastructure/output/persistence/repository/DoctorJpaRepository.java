package com.medisalud.citas.infrastructure.output.persistence.repository;

import com.medisalud.citas.infrastructure.output.persistence.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorJpaRepository extends JpaRepository<DoctorEntity, Long> {
}