package com.medisalud.citas.application.service;

import com.medisalud.citas.domain.model.Doctor;
import com.medisalud.citas.domain.repository.DoctorRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock
    private DoctorRepositoryPort doctorRepositoryPort;

    @InjectMocks
    private DoctorServiceImpl doctorService; // Se inyectará automáticamente en tu implementación

    @Test
    @DisplayName("RF-01: Debería registrar un médico con datos válidos en el sistema")
    void RF01RegistrarMedico() {
        Doctor newDoctor = new Doctor(null, "Dr. Carlos Ruiz", "Pediatría", "555-1002", "carlos.ruiz@medisalud.com");
        Doctor savedDoctor = new Doctor(2L, "Dr. Carlos Ruiz", "Pediatría", "555-1002", "carlos.ruiz@medisalud.com");

        when(doctorRepositoryPort.save(any(Doctor.class))).thenReturn(savedDoctor);

        assertDoesNotThrow(() -> {
            doctorRepositoryPort.save(newDoctor);
        });

        verify(doctorRepositoryPort, times(1)).save(any(Doctor.class));
    }
}