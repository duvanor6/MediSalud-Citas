package com.medisalud.citas.infrastructure.input.controller;

import com.medisalud.citas.application.dto.AvailableSlotDTO;
import com.medisalud.citas.application.dto.request.AppointmentRequestDTO;
import com.medisalud.citas.application.dto.response.AppointmentResponseDTO;
import com.medisalud.citas.application.service.AppointmentService;
import com.medisalud.citas.infrastructure.input.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
@Tag(name = "Citas Médicas", description = "API para el agendamiento, consulta y cancelación de citas")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @Operation(summary = "Agendar cita (RF-03)", description = "Permite a un paciente reservar un turno con un médico")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> scheduleAppointment(@Valid @RequestBody AppointmentRequestDTO request) {
        AppointmentResponseDTO response = appointmentService.scheduleAppointment(request,request.patientBirthDate());
        return new ResponseEntity<>(
                ApiResponse.created(response, "Cita agendada exitosamente"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/franjas-disponibles")
    @Operation(summary = "Consultar franjas disponibles (RF-04)", description = "Devuelve las franjas horarias de 30 mins disponibles para un médico en un rango de fechas")
    public ResponseEntity<ApiResponse<List<AvailableSlotDTO>>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<AvailableSlotDTO> slots = appointmentService.getAvailableSlots(doctorId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(slots, "Franjas disponibles obtenidas con éxito"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar cita (RF-05)", description = "Cancela una cita y aplica penalizaciones si se hace con menos de 2 horas de anticipación")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cita cancelada exitosamente"));
    }

    @PutMapping("/{id}/reprogramar")
    @Operation(summary = "Reprogramar cita (RN-06)", description = "Cambia la fecha/hora de una cita existente validando disponibilidad")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> rescheduleAppointment(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDateTime) {

        AppointmentResponseDTO response = appointmentService.rescheduleAppointment(id, newDateTime);
        return ResponseEntity.ok(ApiResponse.success(response, "Cita reprogramada exitosamente"));
    }

    @GetMapping
    @Operation(summary = "Listado de Citas (RF-06)", description = "Lista las citas usando múltiples filtros opcionales")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDTO>>> listAppointments(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<AppointmentResponseDTO> appointments = appointmentService.listAppointments(doctorId, patientId, status, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(appointments, "Listado de citas obtenido con éxito"));
    }
}
