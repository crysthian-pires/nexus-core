package com.nexus.core.user.dto;

public record UserUpdateResponseDTO(
        UserResponseDTO user,
        String token
) {
}
