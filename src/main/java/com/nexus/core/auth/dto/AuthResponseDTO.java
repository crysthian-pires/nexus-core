package com.nexus.core.auth.dto;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken
) {
}
