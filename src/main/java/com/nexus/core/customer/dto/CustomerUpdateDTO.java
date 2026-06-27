package com.nexus.core.customer.dto;

public record CustomerUpdateDTO(
        String name,
        String email,
        String phone,
        String document,
        String notes
) {}
