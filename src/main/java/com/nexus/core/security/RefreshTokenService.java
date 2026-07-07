package com.nexus.core.security;

import com.nexus.core.exception.InvalidRefreshTokenException;
import com.nexus.core.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String generate(UserModel user) {
        refreshTokenRepository.deleteByUser(user);
        String rawToken = UUID.randomUUID().toString();

        RefreshTokenModel refreshToken = new RefreshTokenModel();
        refreshToken.setToken(hash(rawToken));
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    public RefreshTokenModel validate(String token) {
        return refreshTokenRepository.findByToken(hash(token))
                .filter(rt -> rt.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(InvalidRefreshTokenException::new);
    }

    @Transactional
    public void revokeByUser(UserModel user) {
        refreshTokenRepository.deleteByUser(user);
    }

    private String hash(String value){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashBytes){
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        }catch(NoSuchAlgorithmException e){
            throw new IllegalStateException("SHA-256 não disponível", e);
        }
    }
}
