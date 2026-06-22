package com.medisalud.citas.application.service;

import com.medisalud.citas.domain.model.Patient;
import com.medisalud.citas.domain.repository.PatientRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepositoryPort patientRepositoryPort;

    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    @DisplayName("RF-02: Debería registrar un nuevo paciente de manera exitosa")
    void RF02RegistrarPacientes() {
        Patient newPatient = new Patient(null, "Juan Pérez", "10203040", "555-2001", "juan@correo.com", new ArrayList<>());
        Patient savedPatient = new Patient(1L, "Juan Pérez", "10203040", "555-2001", "juan@correo.com", new ArrayList<>());

        when(patientRepositoryPort.save(any(Patient.class))).thenReturn(savedPatient);

        assertDoesNotThrow(() -> {
            patientRepositoryPort.save(newPatient);
        });

        verify(patientRepositoryPort, times(1)).save(any(Patient.class));
    }
}