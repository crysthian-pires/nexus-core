package com.nexus.core.serviceorder.dto;

import com.nexus.core.serviceorder.ServiceOrderStatus;

import java.math.BigDecimal;

public record ServiceOrderUpdateDTO(
        String description,
        ServiceOrderStatus status,
        BigDecimal totalValue,
        String notes
) {}
