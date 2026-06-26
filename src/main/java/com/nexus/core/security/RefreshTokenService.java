package com.nexus.core.security;

import com.nexus.core.exception.InvalidRefreshTokenException;
import com.nexus.core.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshTokenModel generate(UserModel user) {
        refreshTokenRepository.deleteByUser(user);

        RefreshTokenModel refreshToken = new RefreshTokenModel();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshTokenModel validate(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(InvalidRefreshTokenException::new);
    }

    @Transactional
    public void revokeByUser(UserModel user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
