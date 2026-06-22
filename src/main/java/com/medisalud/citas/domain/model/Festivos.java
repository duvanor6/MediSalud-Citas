package com.medisalud.citas.domain.model;

import java.time.LocalDate;

public enum Festivos {
    ANO_NUEVO(2026, 1, 1, "Año Nuevo"),
    SAN_JOSE(2026, 3, 23, "San José"),
    JUEVES_SANTO(2026, 4, 2, "Jueves Santo"),
    VIERNES_SANTO(2026, 4, 3, "Viernes Santo"),
    DIA_TRABAJO(2026, 5, 1, "Día del Trabajo"),
    CORPUS_CHRISTI(2026, 6, 15, "Corpus Christi"),
    INDEPENDENCIA(2026, 7, 20, "Grito de Independencia"),
    BATALLA_BOYACA(2026, 8, 7, "Batalla de Boyacá"),
    INMACULADA_CONCEPCION(2026, 12, 8, "Inmaculada Concepción"),
    NAVIDAD(2026, 12, 25, "Navidad");

    private final LocalDate date;
    private final String description;

    Festivos(int year, int month, int day, String description) {
        this.date = LocalDate.of(year, month, day);
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Método de utilidad para verificar estáticamente si una fecha es festiva
     */
    public static boolean isHoliday(LocalDate localDate) {
        for (Festivos holiday : values()) {
            if (holiday.getDate().equals(localDate)) {
                return true;
            }
        }
        return false;
    }
}
