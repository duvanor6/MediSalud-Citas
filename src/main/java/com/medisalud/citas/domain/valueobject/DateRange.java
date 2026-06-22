package com.medisalud.citas.domain.valueobject;

import com.medisalud.citas.domain.exception.BusinessRuleException;

import java.time.LocalDate;

public record DateRange(LocalDate startDate, LocalDate endDate) {

    public DateRange {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas del rango no pueden ser nulas");
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessRuleException("La fecha de fin no puede ser anterior a la fecha de inicio del rango");
        }
    }
}
