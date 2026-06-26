package com.nexus.core.security;

import com.nexus.core.exception.InvalidRefreshTokenException;
import com.nexus.core.user.Role;
import com.nexus.core.user.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private UserModel user;
    private RefreshTokenModel validToken;
    private RefreshTokenModel expiredToken;

    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setId(1L);
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setRole(Role.USER);

        validToken = new RefreshTokenModel();
        validToken.setToken("token-valido");
        validToken.setUser(user);
        validToken.setExpiresAt(LocalDateTime.now().plusDays(7));

        expiredToken = new RefreshTokenModel();
        expiredToken.setToken("token-expirado");
        expiredToken.setUser(user);
        expiredToken.setExpiresAt(LocalDateTime.now().minusDays(1));
    }

    @Test
    @DisplayName("Deve gerar refresh token com sucesso")
    void generate_success() {
        when(refreshTokenRepository.save(any(RefreshTokenModel.class))).thenReturn(validToken);

        RefreshTokenModel result = refreshTokenService.generate(user);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshTokenModel.class));
    }

    @Test
    @DisplayName("Deve validar token válido com sucesso")
    void validate_success() {
        when(refreshTokenRepository.findByToken("token-valido"))
                .thenReturn(Optional.of(validToken));

        RefreshTokenModel result = refreshTokenService.validate("token-valido");

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("token-valido");
    }

    @Test
    @DisplayName("Deve lançar exceção para token inexistente")
    void validate_tokenNotFound() {
        when(refreshTokenRepository.findByToken("token-invalido"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.validate("token-invalido"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção para token expirado")
    void validate_tokenExpired() {
        when(refreshTokenRepository.findByToken("token-expirado"))
                .thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> refreshTokenService.validate("token-expirado"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    @DisplayName("Deve revogar token do usuário com sucesso")
    void revokeByUser_success() {
        refreshTokenService.revokeByUser(user);

        verify(refreshTokenRepository).deleteByUser(user);
    }
}
