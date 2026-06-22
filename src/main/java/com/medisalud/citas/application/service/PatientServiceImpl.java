package com.medisalud.citas.application.service;

import com.medisalud.citas.application.dto.request.PatientRequestDTO;
import com.medisalud.citas.application.dto.response.PatientResponseDTO;
import com.medisalud.citas.domain.exception.BusinessRuleException;
import com.medisalud.citas.domain.model.Patient;
import com.medisalud.citas.domain.repository.PatientRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepositoryPort patientRepositoryPort;

    public PatientServiceImpl(PatientRepositoryPort patientRepositoryPort) {
        this.patientRepositoryPort = patientRepositoryPort;
    }

    @Override
    @Transactional
    public PatientResponseDTO registerPatient(PatientRequestDTO request) {
        // Validar si el documento ya existe (Regla implícita del RF-02)
        if (patientRepositoryPort.existsByDocument(request.document())) {
            throw new BusinessRuleException("Ya existe un paciente con el documento: " + request.document());
        }

        Patient patient = new Patient(
                1L, request.name(),
                request.document(),
                request.phone(),
                request.email()
        );

        Patient savedPatient = patientRepositoryPort.save(patient);

        return new PatientResponseDTO(
                savedPatient.getId(),
                savedPatient.getName(),
                savedPatient.getDocument(),
                savedPatient.getPhone(),
                savedPatient.getEmail()
        );
    }
}
