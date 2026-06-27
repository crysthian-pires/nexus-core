package com.nexus.core.serviceorder.dto;

import com.nexus.core.serviceorder.ServiceOrderModel;
import com.nexus.core.serviceorder.ServiceOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceOrderResponseDTO(
        Long id,
        Long customerId,
        String customerName,
        String description,
        ServiceOrderStatus status,
        BigDecimal totalValue,
        String notes,
        LocalDateTime completedAt,
        LocalDateTime createdAt
) {
    public ServiceOrderResponseDTO(ServiceOrderModel order) {
        this(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getDescription(),
                order.getStatus(),
                order.getTotalValue(),
                order.getNotes(),
                order.getCompletedAt(),
                order.getCreatedAt()
        );
    }
}
