package com.nexus.core.serviceorder.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceOrderRequestDTO(

        @NotNull(message = "O cliente é obrigatório")
        Long customerId,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        String description,

        @DecimalMin(value = "0.0", inclusive = false, message = "O valor total deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "O valor total deve ter no máximo 10 dígitos inteiros e 2 decimais")
        BigDecimal totalValue,

        @Size(max = 1000, message = "As observações devem ter no máximo 1000 caracteres")
        String notes

) {
        public ServiceOrderRequestDTO {
                description = description != null ? description.trim() : null;
        }
}
