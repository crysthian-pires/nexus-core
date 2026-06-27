package com.nexus.core.customer.dto;

import com.nexus.core.customer.CustomerModel;

import java.time.LocalDateTime;

public record CustomerResponseDTO(
        Long id,
        String name,
        String email,
        String phone,
        String document,
        String notes,
        boolean active,
        LocalDateTime createdAt
) {
    public CustomerResponseDTO(CustomerModel customer) {
        this(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getDocument(),
                customer.getNotes(),
                customer.isActive(),
                customer.getCreatedAt()
        );
    }
}
