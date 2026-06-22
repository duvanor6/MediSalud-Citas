package com.medisalud.citas.infrastructure.output.persistence.adapter;

import com.medisalud.citas.domain.exception.BusinessRuleException;
import com.medisalud.citas.domain.model.Appointment;
import com.medisalud.citas.domain.model.AppointmentStatus;
import com.medisalud.citas.domain.repository.AppointmentRepositoryPort;
import com.medisalud.citas.infrastructure.output.persistence.entity.AppointmentEntity;
import com.medisalud.citas.infrastructure.output.persistence.entity.DoctorEntity;
import com.medisalud.citas.infrastructure.output.persistence.entity.PatientEntity;
import com.medisalud.citas.infrastructure.output.persistence.repository.AppointmentJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AppointmentPersistenceAdapter implements AppointmentRepositoryPort {

    private final AppointmentJpaRepository repository;
    private final DoctorPersistenceAdapter doctorAdapter;
    private final PatientPersistenceAdapter patientAdapter;

    public AppointmentPersistenceAdapter(AppointmentJpaRepository repository,
                                         DoctorPersistenceAdapter doctorAdapter,
                                         PatientPersistenceAdapter patientAdapter) {
        this.repository = repository;
        this.doctorAdapter = doctorAdapter;
        this.patientAdapter = patientAdapter;
    }

    @Override
    public Appointment save(Appointment appointment) {
        AppointmentEntity entity = new AppointmentEntity();
        entity.setId(appointment.getId());
        entity.setDateTime(appointment.getDateTime());
        entity.setStatus(appointment.getStatus());
        entity.setCancellationDate(appointment.getCancellationDate());
        entity.setHasPenalty(appointment.hasPenalty());

        // Mapeo inverso superficial para las relaciones
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setId(appointment.getPatient().getId());
        entity.setPatient(patientEntity);

        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setId(appointment.getDoctor().getId());
        entity.setDoctor(doctorEntity);

        AppointmentEntity saved = repository.save(entity);

        // Retornamos recargando los datos completos del dominio
        return new Appointment(
                saved.getId(),
                appointment.getPatient(),
                appointment.getDoctor(),
                saved.getDateTime(),
                saved.getStatus(),
                saved.getCancellationDate(),
                saved.isHasPenalty()
        );
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public void validateNoConflicts(Long doctorId, Long patientId, LocalDateTime dateTime) {
        if (repository.existsByDoctorIdAndDateTimeAndStatus(doctorId, dateTime, AppointmentStatus.PROGRAMADA)) {
            throw new BusinessRuleException("RN-02: El médico ya tiene una cita en ese horario");
        }
        if (repository.existsByPatientIdAndDateTimeAndStatus(patientId, dateTime, AppointmentStatus.PROGRAMADA)) {
            throw new BusinessRuleException("RN-04: El paciente ya tiene una cita en ese horario");
        }
    }

    @Override
    public List<Appointment> findByDoctorAndDateRange(Long doctorId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return repository.findByDoctorIdAndDateTimeBetween(doctorId, start, end)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findWithFilters(Long doctorId, Long patientId, String statusStr, LocalDate startDate, LocalDate endDate) {
        AppointmentStatus status = statusStr != null ? AppointmentStatus.valueOf(statusStr.toUpperCase()) : null;
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : null;

        return repository.findWithDynamicFilters(doctorId, patientId, status, start, end)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    private Appointment toDomain(AppointmentEntity entity) {
        return new Appointment(
                entity.getId(),
                patientAdapter.toDomain(entity.getPatient()),
                doctorAdapter.toDomain(entity.getDoctor()),
                entity.getDateTime(),
                entity.getStatus(),
                entity.getCancellationDate(),
                entity.isHasPenalty()
        );
    }
}