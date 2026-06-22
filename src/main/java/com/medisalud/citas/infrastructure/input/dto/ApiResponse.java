package com.medisalud.citas.infrastructure.input.dto;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        LocalDateTime timestamp,
        int status,
        String message,
        T data
) {
    /**
     * Utilidad para respuestas HTTP 200 (OK)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                LocalDateTime.now(),
                200,
                message,
                data
        );
    }

    /**
     * Utilidad para respuestas HTTP 201 (Created)
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(
                LocalDateTime.now(),
                201,
                message,
                data
        );
    }
}
