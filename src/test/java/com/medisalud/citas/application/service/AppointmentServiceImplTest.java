package com.medisalud.citas.application.service;

import com.medisalud.citas.application.dto.request.AppointmentRequestDTO;
import com.medisalud.citas.domain.exception.BusinessRuleException;
import com.medisalud.citas.domain.model.Appointment;
import com.medisalud.citas.domain.model.AppointmentStatus;
import com.medisalud.citas.domain.model.Doctor;
import com.medisalud.citas.domain.model.Patient;
import com.medisalud.citas.domain.repository.AppointmentRepositoryPort;
import com.medisalud.citas.domain.repository.DoctorRepositoryPort;
import com.medisalud.citas.domain.repository.PatientRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepositoryPort appointmentRepositoryPort;

    @Mock
    private DoctorRepositoryPort doctorRepositoryPort;

    @Mock
    private PatientRepositoryPort patientRepositoryPort;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Patient mockPatient;
    private Doctor mockDoctor;
    private AppointmentRequestDTO mockRequest;

    @BeforeEach
    void setUp() {
        mockPatient = new Patient(1L, "Juan Pérez", "10203040", "555-2001", "juan@correo.com", new ArrayList<>());
        mockDoctor = new Doctor(1L, "Dra. María González", "Cardiología", "555-1001", "maria@correo.com");

        mockRequest = mock(AppointmentRequestDTO.class);

        // 🚀 SOLUCIÓN ERROR 2: Usamos Mockito.lenient() para que no falle en los tests
        // donde rescheduleAppointment usa un DTO propio interno.
        Mockito.lenient().when(mockRequest.patientId()).thenReturn(1L);
        Mockito.lenient().when(mockRequest.doctorId()).thenReturn(1L);
    }

    // ==========================================
    // TESTS - RN-01: FRANJAS HORARIAS DE ATENCIÓN
    // ==========================================

    @Test
    @DisplayName("RN-01: Debería fallar si los minutos no pertenecen a franjas exactas de 30 minutos")
    void RN01FallaFranjas30Min() {
        when(mockRequest.dateTime()).thenReturn(LocalDateTime.of(2026, 10, 20, 10, 15));

        when(patientRepositoryPort.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoctor));

        assertThrows(BusinessRuleException.class, () -> {
            appointmentService.scheduleAppointment(mockRequest, null);
        });
    }

    @Test
    @DisplayName("RN-01: Debería fallar si se intenta agendar un Domingo")
    void RN01FallaPorAgendarDomingo() {
        when(mockRequest.dateTime()).thenReturn(LocalDateTime.of(2026, 6, 28, 10, 0));

        when(patientRepositoryPort.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoctor));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            appointmentService.scheduleAppointment(mockRequest, null);
        });
        assertTrue(exception.getMessage().contains("domingo"));
    }

    @Test
    @DisplayName("RN-01: Debería fallar si es Sábado fuera del horario permitido (08:00 - 13:00)")
    void RN01FallaPorSabadoFueraDeHorario() {
        when(mockRequest.dateTime()).thenReturn(LocalDateTime.of(2026, 6, 27, 14, 0));

        when(patientRepositoryPort.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoctor));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            appointmentService.scheduleAppointment(mockRequest, null);
        });
        assertTrue(exception.getMessage().contains("sábados es de 08:00 a 13:00"));
    }

    @Test
    @DisplayName("RN-01: Debería fallar si la fecha es un día Festivo")
    void RN01FallaSiEsFestivo() {
        when(mockRequest.dateTime()).thenReturn(LocalDateTime.of(2026, 12, 25, 10, 0));

        when(patientRepositoryPort.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoctor));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            appointmentService.scheduleAppointment(mockRequest, null);
        });
        assertTrue(exception.getMessage().contains("festivos no hay atención"));
    }

    // ==========================================
    // TESTS - RN-02 y RN-04: CONFLICTOS
    // ==========================================

    @Test
    @DisplayName("RN-02 y RN-04: Debería rechazar la cita si el puerto detecta duplicidad o conflictos")
    void RN02RN04FallaPorDuplicidadOConflictos() {
        when(mockRequest.dateTime()).thenReturn(LocalDateTime.of(2026, 6, 22, 10, 30));

        when(patientRepositoryPort.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoctor));

        doThrow(new BusinessRuleException("RN-02/RN-04: Conflicto detectado"))
                .when(appointmentRepositoryPort).validateNoConflicts(any(), any(), any());

        assertThrows(BusinessRuleException.class, () -> {
            appointmentService.scheduleAppointment(mockRequest, null);
        });
    }

    // ==========================================
    // TESTS - RN-03: ANTIGÜEDAD MÍNIMA
    // ==========================================

    @Test
    @DisplayName("RN-03: Debería fallar si la fecha de nacimiento enviada en la petición es futura")
    void RN03FallaNaciemientoFuturo() {
        when(mockRequest.dateTime()).thenReturn(LocalDateTime.of(2026, 6, 22, 10, 30));

        when(patientRepositoryPort.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoctor));

        LocalDate futureBirthDate = LocalDate.of(2030, 1, 1);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            appointmentService.scheduleAppointment(mockRequest, futureBirthDate);
        });
        assertTrue(exception.getMessage().contains("no puede ser futura"));
    }

    // ==========================================
    // TESTS - RN-05: PENALIZACIONES
    // ==========================================

    @Test
    @DisplayName("RN-05: Debería bloquear el agendamiento si el paciente tiene 3 o más penalizaciones recientes")
    void RN05FallaPorPenalizacion() {
        // 🚀 SOLUCIÓN ERROR 1: Eliminamos el stubbing de mockRequest.dateTime()
        // porque el método lanza la excepción ANTES de llegar a leer la fecha.

        List<LocalDateTime> penalties = List.of(LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        Patient penalizedPatient = new Patient(1L, "Juan", "1020", "555", "j@c.com", penalties);

        when(patientRepositoryPort.findById(1L)).thenReturn(Optional.of(penalizedPatient));
        when(doctorRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoctor));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            appointmentService.scheduleAppointment(mockRequest, null);
        });
        assertTrue(exception.getMessage().contains("3 o más penalizaciones"));
    }

    // ==========================================
    // TESTS - RN-06: REPROGRAMACIÓN
    // ==========================================

    @Test
    @DisplayName("RN-06: Debería ejecutar el flujo de reprogramación cancelando la cita original")
    void RN06ReprogramarCita() {
        LocalDateTime oldTime = LocalDateTime.of(2026, 6, 24, 10, 30);
        LocalDateTime newTime = LocalDateTime.of(2026, 6, 25, 11, 0);

        Appointment oldAppointment = new Appointment(99L, mockPatient, mockDoctor, oldTime, AppointmentStatus.PROGRAMADA, null, false);

        when(appointmentRepositoryPort.findById(99L)).thenReturn(Optional.of(oldAppointment));
        when(patientRepositoryPort.findById(any())).thenReturn(Optional.of(mockPatient));
        when(doctorRepositoryPort.findById(any())).thenReturn(Optional.of(mockDoctor));
        when(appointmentRepositoryPort.save(any(Appointment.class))).thenReturn(oldAppointment);

        assertDoesNotThrow(() -> {
            appointmentService.rescheduleAppointment(99L, newTime);
        });

        verify(appointmentRepositoryPort, atLeastOnce()).save(any(Appointment.class));
    }

    // ==========================================
    // TESTS - REQUERIMIENTOS FUNCIONALES (RF)
    // ==========================================

    @Test
    @DisplayName("RF-03: Debería reservar una cita exitosamente")
    void RF03ReservarCitaExitosamente() {
        LocalDateTime validTime = LocalDateTime.of(2026, 6, 24, 10, 30); // Miércoles 10:30 AM
        when(mockRequest.dateTime()).thenReturn(validTime);

        when(patientRepositoryPort.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoctor));

        Appointment expectedAppointment = new Appointment(100L, mockPatient, mockDoctor, validTime, AppointmentStatus.PROGRAMADA, null, false);
        when(appointmentRepositoryPort.save(any(Appointment.class))).thenReturn(expectedAppointment);

        var response = appointmentService.scheduleAppointment(mockRequest, null);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("PROGRAMADA", response.status());
    }

    @Test
    @DisplayName("RF-04: Debería consultar las franjas disponibles de un médico (Excluyendo las ocupadas)")
    void RF04ConsultarFranjas() {
        LocalDate startDate = LocalDate.of(2026, 6, 22); // Lunes
        LocalDate endDate = LocalDate.of(2026, 6, 22);

        when(doctorRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoctor));
        // Simulamos que no hay citas ocupadas para que devuelva todas las franjas del lunes (8:00 a 18:00 = 20 franjas)
        when(appointmentRepositoryPort.findByDoctorAndDateRange(1L, startDate, endDate)).thenReturn(new ArrayList<>());

        var slots = appointmentService.getAvailableSlots(1L, startDate, endDate);

        assertNotNull(slots);
        assertEquals(20, slots.size(), "Un lunes completo debe tener 20 franjas de 30 minutos");
    }

    @Test
    @DisplayName("RF-05: Debería cambiar el estado a CANCELADA y guardar la fecha de cancelación")
    void RF05CancelarCitasCorrectamente() {
        LocalDateTime appointmentTime = LocalDateTime.of(2026, 6, 26, 16, 0); // En el futuro
        Appointment appointment = new Appointment(1L, mockPatient, mockDoctor, appointmentTime, AppointmentStatus.PROGRAMADA, null, false);

        when(appointmentRepositoryPort.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepositoryPort.save(any(Appointment.class))).thenReturn(appointment);

        assertDoesNotThrow(() -> appointmentService.cancelAppointment(1L));
        assertEquals(AppointmentStatus.CANCELADA, appointment.getStatus());
        assertNotNull(appointment.getCancellationDate());
    }

    @Test
    @DisplayName("RF-06: Debería listar citas aplicando filtros dinámicos opcionales")
    void RF06ListarCitasConFiltros() {
        LocalDate start = LocalDate.of(2026, 6, 22);
        LocalDate end = LocalDate.of(2026, 6, 25);

        Appointment app = new Appointment(1L, mockPatient, mockDoctor, LocalDateTime.of(2026, 6, 22, 10, 30), AppointmentStatus.PROGRAMADA, null, false);
        when(appointmentRepositoryPort.findWithFilters(1L, 1L, "PROGRAMADA", start, end)).thenReturn(List.of(app));

        var result = appointmentService.listAppointments(1L, 1L, "PROGRAMADA", start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }
}

