package com.medisalud.citas.application.dto.response;

import java.time.LocalDateTime;

public record AppointmentResponseDTO(
        Long id,
        PatientResponseDTO patient,
        DoctorResponseDTO doctor,
        LocalDateTime dateTime,
        String status
) {}