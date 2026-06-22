package com.medisalud.citas.application.service;

import com.medisalud.citas.application.dto.AvailableSlotDTO;
import com.medisalud.citas.application.dto.request.AppointmentRequestDTO;
import com.medisalud.citas.application.dto.response.AppointmentResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    AppointmentResponseDTO scheduleAppointment(AppointmentRequestDTO request, java.time.LocalDate patientBirthDate);
    List<AvailableSlotDTO> getAvailableSlots(Long doctorId, LocalDate startDate, LocalDate endDate);
    void cancelAppointment(Long appointmentId);
    AppointmentResponseDTO rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime);
    List<AppointmentResponseDTO> listAppointments(Long doctorId, Long patientId, String status, LocalDate startDate, LocalDate endDate);
}