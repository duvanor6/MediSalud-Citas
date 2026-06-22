package com.medisalud.citas.domain.model;

import com.medisalud.citas.domain.valueobject.TimeSlot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Doctor {
    private Long id;
    private String name;
    private String specialty;
    private String phone;
    private String email;

    public Doctor(Long id, String name, String specialty, String phone, String email) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.phone = phone;
        this.email = email;
    }

    public Doctor(String name, String specialty, String phone, String email) {
        this(null, name, specialty, phone, email);
    }

    // RF-04 y RN-01: Calcula las franjas disponibles basándose en el horario laboral y citas ya tomadas
    public List<TimeSlot> calculateAvailableSlots(LocalDate startDate, LocalDate endDate, List<Appointment> bookedAppointments) {
        List<TimeSlot> allPossibleSlots = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            allPossibleSlots.addAll(generateDailyWorkingSlots(currentDate));
            currentDate = currentDate.plusDays(1);
        }

        List<LocalDateTime> bookedStartTimes = bookedAppointments.stream()
                .filter(app -> app.getStatus() == AppointmentStatus.PROGRAMADA)
                .map(Appointment::getDateTime)
                .collect(Collectors.toList());

        // Retorna solo los slots que NO están en la lista de citas agendadas
        return allPossibleSlots.stream()
                .filter(slot -> !bookedStartTimes.contains(slot.startTime()))
                .collect(Collectors.toList());
    }

    // RN-01: Reglas de horario laboral
    private List<TimeSlot> generateDailyWorkingSlots(LocalDate date) {
        List<TimeSlot> dailySlots = new ArrayList<>();
        DayOfWeek day = date.getDayOfWeek();

        if (day == DayOfWeek.SUNDAY) {
            return dailySlots; // Sin atención
        }

        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = day == DayOfWeek.SATURDAY ? LocalTime.of(13, 0) : LocalTime.of(18, 0);

        while (startTime.isBefore(endTime)) {
            LocalDateTime slotStart = LocalDateTime.of(date, startTime);
            LocalDateTime slotEnd = slotStart.plusMinutes(30);
            dailySlots.add(new TimeSlot(slotStart, slotEnd));
            startTime = startTime.plusMinutes(30);
        }

        return dailySlots;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setId(Long id) { this.id = id; }
}