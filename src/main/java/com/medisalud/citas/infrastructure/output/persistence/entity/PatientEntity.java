package com.medisalud.citas.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class PatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String document;

    private String phone;
    private String email;

    // Persistimos la lista de fechas de penalización en una tabla anexa automáticamente
    @ElementCollection
    @CollectionTable(name = "patient_penalties", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "penalty_date")
    private List<LocalDateTime> penaltyDates;
}