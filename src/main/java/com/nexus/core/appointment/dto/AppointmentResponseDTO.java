package com.nexus.core.appointment.dto;

import com.nexus.core.appointment.AppointmentModel;
import com.nexus.core.appointment.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppointmentResponseDTO(
        Long id,
        Long customerId,
        String customerName,
        Long serviceOrderId,
        String description,
        LocalDateTime scheduledAt,
        AppointmentStatus status,
        BigDecimal estimatedValue,
        String notes,
        LocalDateTime createdAt
) {
    public AppointmentResponseDTO(AppointmentModel appointment) {
        this(
                appointment.getId(),
                appointment.getCustomer().getId(),
                appointment.getCustomer().getName(),
                appointment.getServiceOrder() != null ? appointment.getServiceOrder().getId() : null,
                appointment.getDescription(),
                appointment.getScheduledAt(),
                appointment.getStatus(),
                appointment.getEstimatedValue(),
                appointment.getNotes(),
                appointment.getCreatedAt()
        );
    }
}
