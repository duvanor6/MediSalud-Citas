package com.medisalud.citas.infrastructure.output.persistence.repository;

import com.medisalud.citas.domain.model.AppointmentStatus;
import com.medisalud.citas.infrastructure.output.persistence.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, Long> {

    // Para RN-02 y RN-04: Buscar conflictos de horario (Citas PROGRAMADAS en la misma fecha y hora exacta)
    boolean existsByDoctorIdAndDateTimeAndStatus(Long doctorId, LocalDateTime dateTime, AppointmentStatus status);
    boolean existsByPatientIdAndDateTimeAndStatus(Long patientId, LocalDateTime dateTime, AppointmentStatus status);

    // Para RF-04: Buscar citas de un médico en un rango de fechas
    List<AppointmentEntity> findByDoctorIdAndDateTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    // Para RF-06: Filtros dinámicos usando JPQL
    @Query("SELECT a FROM AppointmentEntity a " +
            "WHERE (:doctorId IS NULL OR a.doctor.id = :doctorId) " +
            "AND (:patientId IS NULL OR a.patient.id = :patientId) " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:start IS NULL OR a.dateTime >= :start) " +
            "AND (:end IS NULL OR a.dateTime <= :end)")
    List<AppointmentEntity> findWithDynamicFilters(
            @Param("doctorId") Long doctorId,
            @Param("patientId") Long patientId,
            @Param("status") AppointmentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}