package com.medisalud.citas.domain.model;

import com.medisalud.citas.domain.exception.BusinessRuleException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Patient {
    private Long id;
    private String name;
    private String document;
    private String phone;
    private String email;
    private List<LocalDateTime> penaltyDates; // Fechas donde recibió penalización

    public Patient(Long id, String name, String document, String phone, String email, List<LocalDateTime> penaltyDates) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.phone = phone;
        this.email = email;
        this.penaltyDates = penaltyDates != null ? penaltyDates : new ArrayList<>();
    }

    public Patient(long l, String name, String document, String phone, String email) {
        this(null, name, document, phone, email, new ArrayList<>());
    }

    // RN-05: Penalización por Cancelación Tardía
    public void validateCanScheduleAppointments() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long recentPenalties = penaltyDates.stream()
                .filter(date -> date.isAfter(thirtyDaysAgo))
                .count();

        if (recentPenalties >= 3) {
            throw new BusinessRuleException("El paciente tiene 3 o más penalizaciones en los últimos 30 días y no puede agendar citas.");
        }
    }

    public void addPenalty(LocalDateTime penaltyDate) {
        this.penaltyDates.add(penaltyDate);
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDocument() { return document; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public List<LocalDateTime> getPenaltyDates() { return penaltyDates; }

    public void setId(Long id) { this.id = id; }
}