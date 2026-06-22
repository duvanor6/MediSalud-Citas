package com.medisalud.citas.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AppointmentRequestDTO(
        @NotNull(message = "El ID del paciente es obligatorio")
        Long patientId,

        @NotNull(message = "El ID del médico es obligatorio")
        Long doctorId,

        @NotNull(message = "La fecha y hora de la cita son obligatorias")
        LocalDateTime dateTime,

        LocalDate patientBirthDate
) {}