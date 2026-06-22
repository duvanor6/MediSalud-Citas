package com.medisalud.citas.domain.model;

import com.medisalud.citas.domain.exception.BusinessRuleException;

import java.time.Duration;
import java.time.LocalDateTime;

public class Appointment {
    private Long id;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;
    private AppointmentStatus status;
    private LocalDateTime cancellationDate;
    private boolean hasPenalty;

    public Appointment(Long id, Patient patient, Doctor doctor, LocalDateTime dateTime, AppointmentStatus status, LocalDateTime cancellationDate, boolean hasPenalty) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.status = status;
        this.cancellationDate = cancellationDate;
        this.hasPenalty = hasPenalty;
    }

    public static Appointment create(Patient patient, Doctor doctor, LocalDateTime dateTime) {
        if (dateTime.getMinute() != 0 && dateTime.getMinute() != 30) {
            throw new BusinessRuleException("Las citas deben ser en franjas exactas de 30 minutos (ej: 10:00 o 10:30)");
        }

        // RN-01 validación superficial (el repositorio hace el resto)
        int hour = dateTime.getHour();
        if (hour < 8 || hour >= 18) {
            throw new BusinessRuleException("El horario de la cita está fuera del horario laboral (08:00 - 18:00)");
        }

        return new Appointment(null, patient, doctor, dateTime, AppointmentStatus.PROGRAMADA, null, false);
    }

    // RN-05: Lógica de cancelación y cálculo de penalizaciones
    public void cancel(LocalDateTime now) {
        if (this.status != AppointmentStatus.PROGRAMADA) {
            throw new BusinessRuleException("Solo se pueden cancelar citas en estado PROGRAMADA");
        }

        this.status = AppointmentStatus.CANCELADA;
        this.cancellationDate = now;

        // Si se cancela con menos de 2 horas de antelación
        long hoursBetween = Duration.between(now, this.dateTime).toHours();
        if (hoursBetween < 2 && now.isBefore(this.dateTime)) {
            this.hasPenalty = true;
        }
    }

    // Getters
    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public Doctor getDoctor() { return doctor; }
    public LocalDateTime getDateTime() { return dateTime; }
    public AppointmentStatus getStatus() { return status; }
    public LocalDateTime getCancellationDate() { return cancellationDate; }
    public boolean hasPenalty() { return hasPenalty; }

    public void setId(Long id) { this.id = id; }
}