package com.nexus.core.user.dto;

import com.nexus.core.user.UserModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate birthDate,
        boolean active,
        LocalDateTime createdAt
) {
    public UserResponseDTO(UserModel user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthDate(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
