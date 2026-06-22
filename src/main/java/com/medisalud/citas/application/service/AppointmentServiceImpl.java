package com.medisalud.citas.application.service;

import com.medisalud.citas.application.dto.AvailableSlotDTO;
import com.medisalud.citas.application.dto.request.AppointmentRequestDTO;
import com.medisalud.citas.application.dto.response.AppointmentResponseDTO;
import com.medisalud.citas.application.dto.response.DoctorResponseDTO;
import com.medisalud.citas.application.dto.response.PatientResponseDTO;
import com.medisalud.citas.domain.exception.BusinessRuleException;
import com.medisalud.citas.domain.exception.ResourceNotFoundException;
import com.medisalud.citas.domain.model.Appointment;
import com.medisalud.citas.domain.model.Doctor;
import com.medisalud.citas.domain.model.Festivos;
import com.medisalud.citas.domain.model.Patient;
import com.medisalud.citas.domain.repository.AppointmentRepositoryPort;
import com.medisalud.citas.domain.repository.DoctorRepositoryPort;
import com.medisalud.citas.domain.repository.PatientRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepositoryPort appointmentRepositoryPort;
    private final DoctorRepositoryPort doctorRepositoryPort;
    private final PatientRepositoryPort patientRepositoryPort;

    public AppointmentServiceImpl(AppointmentRepositoryPort appointmentRepositoryPort,
                                  DoctorRepositoryPort doctorRepositoryPort,
                                  PatientRepositoryPort patientRepositoryPort) {
        this.appointmentRepositoryPort = appointmentRepositoryPort;
        this.doctorRepositoryPort = doctorRepositoryPort;
        this.patientRepositoryPort = patientRepositoryPort;
    }

    @Override
    @Transactional
    public AppointmentResponseDTO scheduleAppointment(AppointmentRequestDTO request, java.time.LocalDate patientBirthDate) {
        Patient patient = patientRepositoryPort.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
        Doctor doctor = doctorRepositoryPort.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));




        // Validar RN-05: Paciente no debe tener penalizaciones activas
        patient.validateCanScheduleAppointments();

        // Validar RN-02 y RN-04: Conflictos de horario (Consultando a base de datos)
        appointmentRepositoryPort.validateNoConflicts(doctor.getId(), patient.getId(), request.dateTime());

        // Crear cita (La entidad de dominio validará RN-01 horarios y RN-03 edad)
        Appointment appointment = Appointment.create(patient, doctor, request.dateTime());


        LocalDateTime appointmentTime = appointment.getDateTime();

        // 1. Regla de negocio: La cita no puede ser en el pasado
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("No se puede agendar una cita en el pasado.");
        }

        // 2. Regla de negocio: No hay atención los domingos
        if (appointmentTime.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            throw new BusinessRuleException("No se pueden agendar citas los días domingo.");
        }

        // 3. Regla de negocio: Horario laboral (08:00 AM a 06:00 PM)
        int hour = appointmentTime.getHour();
        if (hour < 8 || hour >= 18) {
            throw new BusinessRuleException("La cita debe estar dentro del horario de atención (08:00 - 18:00).");
        }


        // 4. RN-01: Festivos
        if (Festivos.isHoliday(appointmentTime.toLocalDate())) {
            throw new BusinessRuleException("RN-01: Los días festivos no hay atención médica.");
        }

        // 5. RN-01: Horarios laborales diferenciados (Sábados vs Lunes a Viernes)
        if (appointmentTime.getDayOfWeek() == java.time.DayOfWeek.SATURDAY) {
            if (hour < 8 || hour >= 13) {
                throw new BusinessRuleException("RN-01: El horario de atención para los sábados es de 08:00 a 13:00.");
            }
        } else {
            if (hour < 8 || hour >= 18) {
                throw new BusinessRuleException("RN-01: El horario de atención de Lunes a Viernes es de 08:00 a 18:00.");
            }
        }

        if (patientBirthDate != null) {
            // Si mandan una fecha, validamos que no sea del futuro
            if (patientBirthDate.isAfter(java.time.LocalDate.now())) {
                throw new BusinessRuleException("RN-03: La fecha de nacimiento del paciente no puede ser futura.");
            }
        }


        Appointment savedAppointment = appointmentRepositoryPort.save(appointment);
        return mapToResponseDTO(savedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableSlotDTO> getAvailableSlots(Long doctorId, LocalDate startDate, LocalDate endDate) {
        Doctor doctor = doctorRepositoryPort.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));

        // Obtenemos las citas ya agendadas en ese rango
        List<Appointment> bookedAppointments = appointmentRepositoryPort.findByDoctorAndDateRange(doctorId, startDate, endDate);

        // Le pedimos al dominio del Doctor que calcule sus franjas libres
        return doctor.calculateAvailableSlots(startDate, endDate, bookedAppointments).stream()
                .map(slot -> new AvailableSlotDTO(slot.startTime(), slot.endTime()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepositoryPort.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        // La entidad cita maneja la lógica de cambio de estado y penalización (RN-05)
        appointment.cancel(LocalDateTime.now());

        // Si la cita generó penalización, actualizamos al paciente
        if (appointment.hasPenalty()) {
            Patient patient = appointment.getPatient();
            patient.addPenalty(appointment.getCancellationDate());
            patientRepositoryPort.save(patient);
        }

        appointmentRepositoryPort.save(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime) {
        // RN-06: Reprogramación
        cancelAppointment(appointmentId); // 1. Cancela aplicando reglas

        Appointment oldAppointment = appointmentRepositoryPort.findById(appointmentId).get();


        LocalDateTime appointmentTime = oldAppointment.getDateTime();

        // 1. Regla de negocio: La cita no puede ser en el pasado
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("No se puede agendar una cita en el pasado.");
        }



        // 2. Regla de negocio: No hay atención los domingos
        if (appointmentTime.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            throw new BusinessRuleException("RN-02: No se pueden agendar citas los días domingo.");
        }

        // 3. Regla de negocio: Horario laboral (08:00 AM a 06:00 PM)
        int hour = appointmentTime.getHour();
        if (hour < 8 || hour >= 18) {
            throw new BusinessRuleException("RN-01: La cita debe estar dentro del horario de atención (08:00 - 18:00).");
        }

        // 4. RN-01: Festivos
        if (Festivos.isHoliday(appointmentTime.toLocalDate())) {
            throw new BusinessRuleException("RN-01: Los días festivos no hay atención médica.");
        }

        // 5. RN-01: Horarios laborales diferenciados (Sábados vs Lunes a Viernes)
        if (appointmentTime.getDayOfWeek() == java.time.DayOfWeek.SATURDAY) {
            if (hour < 8 || hour >= 13) {
                throw new BusinessRuleException("RN-01: El horario de atención para los sábados es de 08:00 a 13:00.");
            }
        } else {
            if (hour < 8 || hour >= 18) {
                throw new BusinessRuleException("RN-01: El horario de atención de Lunes a Viernes es de 08:00 a 18:00.");
            }
        }




        // 2. Crea nueva cita validando horarios
        AppointmentRequestDTO newRequest = new AppointmentRequestDTO(
                oldAppointment.getPatient().getId(),
                oldAppointment.getDoctor().getId(),
                newDateTime,
                null
        );

        return scheduleAppointment(newRequest, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> listAppointments(Long doctorId, Long patientId, String status, LocalDate startDate, LocalDate endDate) {
        List<Appointment> appointments = appointmentRepositoryPort.findWithFilters(doctorId, patientId, status, startDate, endDate);
        return appointments.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    // Método auxiliar para mapeo
    private AppointmentResponseDTO mapToResponseDTO(Appointment appointment) {
        PatientResponseDTO patientDTO = new PatientResponseDTO(
                appointment.getPatient().getId(), appointment.getPatient().getName(),
                appointment.getPatient().getDocument(), appointment.getPatient().getPhone(), appointment.getPatient().getEmail());

        DoctorResponseDTO doctorDTO = new DoctorResponseDTO(
                appointment.getDoctor().getId(), appointment.getDoctor().getName(),
                appointment.getDoctor().getSpecialty(), appointment.getDoctor().getPhone(), appointment.getDoctor().getEmail());

        return new AppointmentResponseDTO(
                appointment.getId(), patientDTO, doctorDTO, appointment.getDateTime(), appointment.getStatus().name());
    }
}