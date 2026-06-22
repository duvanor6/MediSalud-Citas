package com.medisalud.citas.infrastructure.input.controller;

import com.medisalud.citas.application.dto.request.DoctorRequestDTO;
import com.medisalud.citas.application.dto.response.DoctorResponseDTO;
import com.medisalud.citas.application.service.DoctorService;
import com.medisalud.citas.infrastructure.input.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctores")
@Tag(name = "Médicos", description = "API para la gestión de personal médico")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo médico", description = "Crea un médico en el sistema cumpliendo el RF-01")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> registerDoctor(@Valid @RequestBody DoctorRequestDTO request) {
        DoctorResponseDTO response = doctorService.registerDoctor(request);
        return new ResponseEntity<>(
                ApiResponse.created(response, "Médico registrado exitosamente"),
                HttpStatus.CREATED
        );
    }
}
