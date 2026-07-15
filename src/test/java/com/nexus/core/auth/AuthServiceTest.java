package com.nexus.core.auth;


import com.nexus.core.auth.dto.UserLoginDTO;
import com.nexus.core.auth.exception.DisabledUserException;

import com.nexus.core.security.JwtService;
import com.nexus.core.security.RefreshTokenService;
import com.nexus.core.user.UserModel;
import com.nexus.core.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;


    @Test
    @DisplayName("Deve retornar tokens quando as credenciais são válidas")
    void should_return_tokens_when_credentials_are_valid() {
        UserModel user = new UserModel();
        user.setEmail("joao@email.com");
        user.setPassword("hash-da-senha");
        user.setActive(true);

        UserLoginDTO dto = new UserLoginDTO("joao@email.com", "senha123");

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha123", "hash-da-senha")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("access-token-fake");
        when(refreshTokenService.generate(user)).thenReturn("refresh-token-fake");

        var response = authService.login(dto);

        assertThat(response.accessToken()).isEqualTo("access-token-fake");
        assertThat(response.refreshToken()).isEqualTo("refresh-token-fake");

        verify(jwtService, times(1)).generateToken(user);
        verify(refreshTokenService, times(1)).generate(user);
    }


    @Test
    @DisplayName("Deve lançar exceção quando o usuário não existe")
    void should_throw_exception_when_user_does_not_exist() {
        UserLoginDTO dto = new UserLoginDTO("naoexiste@email.com", "qualquersenha");
        when(userRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.login(dto));

        verify(jwtService, never()).generateToken(any());
        verify(refreshTokenService, never()).generate(any());
    }


    @Test
    @DisplayName("Deve lançar exceção quando o usuário está desativado, sem gerar nenhum token")
    void should_throw_exception_and_never_generate_tokens_when_user_is_deactivated() {
        UserModel user = new UserModel();
        user.setEmail("desativado@email.com");
        user.setPassword("hash-da-senha");
        user.setActive(false);

        UserLoginDTO dto = new UserLoginDTO("desativado@email.com", "senha123");
        when(userRepository.findByEmail("desativado@email.com")).thenReturn(Optional.of(user));

        assertThrows(DisabledUserException.class, () -> authService.login(dto));

        verify(jwtService, never()).generateToken(any());
        verify(refreshTokenService, never()).generate(any());
    }


    @Test
    @DisplayName("Deve lançar exceção quando a senha está incorreta")
    void should_throw_exception_when_password_is_incorrect() {
        UserModel user = new UserModel();
        user.setEmail("joao@email.com");
        user.setPassword("hash-da-senha");
        user.setActive(true);

        UserLoginDTO dto = new UserLoginDTO("joao@email.com", "senha-errada");

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha-errada", "hash-da-senha")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(dto));

        verify(jwtService, never()).generateToken(any());
        verify(refreshTokenService, never()).generate(any());
    }
}
