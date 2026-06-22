package com.medisalud.citas.application.dto;

import java.time.LocalDateTime;

public record AvailableSlotDTO(
        LocalDateTime startTime,
        LocalDateTime endTime
) {}