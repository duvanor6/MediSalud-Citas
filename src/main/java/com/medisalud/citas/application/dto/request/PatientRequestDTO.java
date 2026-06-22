package com.medisalud.citas.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatientRequestDTO(
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String name,

        @NotBlank(message = "El documento de identidad es obligatorio")
        @Size(min = 7, message = "El documento debe tener al menos 7 caracteres")
        String document,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(min = 7, message = "El teléfono debe tener al menos 7 dígitos")
        String phone,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email


) {}