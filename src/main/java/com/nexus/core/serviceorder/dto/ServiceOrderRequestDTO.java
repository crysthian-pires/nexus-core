package com.nexus.core.serviceorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ServiceOrderRequestDTO(

        @NotNull(message = "O cliente é obrigatório")
        Long customerId,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        BigDecimal totalValue,
        String notes

) {}
