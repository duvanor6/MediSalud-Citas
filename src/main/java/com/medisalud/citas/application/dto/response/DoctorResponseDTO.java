package com.medisalud.citas.application.dto.response;

public record DoctorResponseDTO(
        Long id,
        String name,
        String specialty,
        String phone,
        String email
) {}