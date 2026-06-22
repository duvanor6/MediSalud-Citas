package com.medisalud.citas.domain.repository;

import com.medisalud.citas.domain.model.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepositoryPort {

    Appointment save(Appointment appointment);

    Optional<Appointment> findById(Long id);

    /**
     * Valida que no existan conflictos de horario para el médico (RN-02)
     * ni para el paciente (RN-04) en la fecha y hora indicadas.
     * Si existe un conflicto, la implementación deberá lanzar una BusinessRuleException.
     *
     * @param doctorId ID del médico
     * @param patientId ID del paciente
     * @param dateTime Fecha y hora a validar
     */
    void validateNoConflicts(Long doctorId, Long patientId, LocalDateTime dateTime);

    /**
     * RF-04: Consulta de Citas Disponibles
     * Devuelve las citas ya agendadas de un médico en un rango de fechas.
     * Esto servirá para que el dominio calcule qué franjas quedan libres.
     *
     * @param doctorId ID del médico
     * @param startDate Fecha de inicio (inclusive)
     * @param endDate Fecha de fin (inclusive)
     * @return Lista de citas en ese rango
     */
    List<Appointment> findByDoctorAndDateRange(Long doctorId, LocalDate startDate, LocalDate endDate);

    /**
     * RF-06: Listado de Citas
     * Busca citas aplicando filtros dinámicos y opcionales.
     *
     * @param doctorId (Opcional)
     * @param patientId (Opcional)
     * @param status (Opcional) ej. PROGRAMADA, CANCELADA, ATENDIDA
     * @param startDate (Opcional) Rango inicial
     * @param endDate (Opcional) Rango final
     * @return Lista de citas que coinciden con los filtros
     */
    List<Appointment> findWithFilters(Long doctorId, Long patientId, String status, LocalDate startDate, LocalDate endDate);
}
