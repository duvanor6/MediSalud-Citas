package com.medisalud.citas.application.service;

import com.medisalud.citas.application.dto.request.DoctorRequestDTO;
import com.medisalud.citas.application.dto.response.DoctorResponseDTO;
import com.medisalud.citas.domain.model.Doctor;
import com.medisalud.citas.domain.repository.DoctorRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepositoryPort doctorRepositoryPort;

    public DoctorServiceImpl(DoctorRepositoryPort doctorRepositoryPort) {
        this.doctorRepositoryPort = doctorRepositoryPort;
    }

    @Override
    @Transactional
    public DoctorResponseDTO registerDoctor(DoctorRequestDTO request) {
        // 1. Mapear DTO a Entidad de Dominio
        Doctor doctor = new Doctor(
                request.name(),
                request.specialty(),
                request.phone(),
                request.email()
        );

        // 2. Guardar a través del puerto
        Doctor savedDoctor = doctorRepositoryPort.save(doctor);

        // 3. Retornar DTO de respuesta
        return new DoctorResponseDTO(
                savedDoctor.getId(),
                savedDoctor.getName(),
                savedDoctor.getSpecialty(),
                savedDoctor.getPhone(),
                savedDoctor.getEmail()
        );
    }
}
