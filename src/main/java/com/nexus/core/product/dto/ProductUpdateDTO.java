package com.nexus.core.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductUpdateDTO(

        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String name,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String description,

        @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "O preço deve ter no máximo 10 dígitos inteiros e 2 decimais")
        BigDecimal price,

        @Min(value = 0, message = "A quantidade não pode ser negativa")
        Integer quantity,

        @Size(max = 60, message = "A categoria deve ter no máximo 60 caracteres")
        String category

) {
        public ProductUpdateDTO {
                name = name != null ? name.trim() : null;
        }
}
