package com.medisalud.citas.infrastructure.input.controller;

import com.medisalud.citas.application.dto.request.PatientRequestDTO;
import com.medisalud.citas.application.dto.response.PatientResponseDTO;
import com.medisalud.citas.application.service.PatientService;
import com.medisalud.citas.infrastructure.input.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pacientes")
@Tag(name = "Pacientes", description = "API para la gestión de pacientes")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo paciente", description = "Crea un paciente en el sistema cumpliendo el RF-02")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> registerPatient(@Valid @RequestBody PatientRequestDTO request) {
        PatientResponseDTO response = patientService.registerPatient(request);
        return new ResponseEntity<>(
                ApiResponse.created(response, "Paciente registrado exitosamente"),
                HttpStatus.CREATED
        );
    }
}