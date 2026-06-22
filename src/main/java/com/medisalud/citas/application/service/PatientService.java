package com.medisalud.citas.application.service;

import com.medisalud.citas.application.dto.request.PatientRequestDTO;
import com.medisalud.citas.application.dto.response.PatientResponseDTO;

public interface PatientService {
    PatientResponseDTO registerPatient(PatientRequestDTO request);
}
