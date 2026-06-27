package com.nexus.core.appointment.dto;

import com.nexus.core.appointment.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppointmentUpdateDTO(
        String description,
        LocalDateTime scheduledAt,
        AppointmentStatus status,
        BigDecimal estimatedValue,
        String notes
) {}
