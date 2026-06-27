package com.nexus.core.customer.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        String name,

        String email,
        String phone,
        String document,
        String notes

) {}
