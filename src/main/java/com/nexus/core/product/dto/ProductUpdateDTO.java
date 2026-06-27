package com.nexus.core.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductUpdateDTO(

        String name,

        String description,

        @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
        BigDecimal price,

        @Min(value = 0, message = "A quantidade não pode ser negativa")
        Integer quantity,

        String category

) {}
