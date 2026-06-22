package com.medisalud.citas.application.service;


import com.medisalud.citas.application.dto.request.DoctorRequestDTO;
import com.medisalud.citas.application.dto.response.DoctorResponseDTO;

public interface DoctorService {
    DoctorResponseDTO registerDoctor(DoctorRequestDTO request);
}
