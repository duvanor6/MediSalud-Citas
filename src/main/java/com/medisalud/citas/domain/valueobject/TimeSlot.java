package com.medisalud.citas.domain.valueobject;


import com.medisalud.citas.domain.exception.BusinessRuleException;

import java.time.Duration;
import java.time.LocalDateTime;

public record TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {

    // El constructor compacto del record nos permite añadir validaciones inmutables
    public TimeSlot {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas");
        }
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new BusinessRuleException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // RN-01: Las citas solo pueden programarse en franjas de 30 minutos
        long minutes = Duration.between(startTime, endTime).toMinutes();
        if (minutes != 30) {
            throw new BusinessRuleException("Las franjas horarias de atención deben ser exactamente de 30 minutos");
        }
    }
}