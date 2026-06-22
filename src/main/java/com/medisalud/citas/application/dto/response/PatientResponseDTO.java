package com.medisalud.citas.application.dto.response;

public record PatientResponseDTO(
        Long id,
        String name,
        String document,
        String phone,
        String email
) {}