package com.nexus.core.user;

import com.nexus.core.exception.EmailAlreadyExistsException;
import com.nexus.core.exception.UserNotFoundException;
import com.nexus.core.security.JwtService;
import com.nexus.core.security.RefreshTokenModel;
import com.nexus.core.security.RefreshTokenRepository;
import com.nexus.core.security.RefreshTokenService;
import com.nexus.core.user.dto.UserResponseDTO;
import com.nexus.core.user.dto.UserSignUpDTO;
import com.nexus.core.user.dto.UserUpdateDTO;
import com.nexus.core.user.dto.UserUpdateResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private UserModel user;

    @Mock
    private RefreshTokenService refreshTokenService;


    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setId(1L);
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setPassword("senha_hash");
        user.setRole(Role.USER);
        user.setActive(true);
    }


    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void create_success() {
        UserSignUpDTO dto = new UserSignUpDTO("João Silva", "joao@email.com", "123456");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("senha_hash");
        when(userRepository.save(any(UserModel.class))).thenReturn(user);

        UserResponseDTO response = userService.create(dto);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("joao@email.com");
        verify(userRepository).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com email duplicado")
    void create_emailDuplicated() {
        UserSignUpDTO dto = new UserSignUpDTO("João Silva", "joao@email.com", "123456");

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }


    @Test
    @DisplayName("Deve retornar usuário por ID com sucesso")
    void findById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void findById_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(UserNotFoundException.class);
    }


    @Test
    @DisplayName("Deve atualizar nome e email com sucesso")
    void update_success() {
        UserUpdateDTO dto = new UserUpdateDTO("João Atualizado", "novo@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserModel.class))).thenReturn(user);
        when(jwtService.generateToken(any(UserModel.class))).thenReturn("novo_token");

        UserUpdateResponseDTO response = userService.update(1L, dto);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("novo_token");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar para email já em uso")
    void update_emailAlreadyExists() {
        UserUpdateDTO dto = new UserUpdateDTO("João", "existente@email.com");
        UserModel outroUsuario = new UserModel();
        outroUsuario.setId(2L);
        outroUsuario.setEmail("existente@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("existente@email.com")).thenReturn(Optional.of(outroUsuario));

        assertThatThrownBy(() -> userService.update(1L, dto))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }


    @Test
    @DisplayName("Deve desativar usuário com sucesso")
    void deactivate_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deactivate(1L);
        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar usuário inexistente")
    void deactivate_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.deactivate(999L))
                .isInstanceOf(UserNotFoundException.class);
    }


    @Test
    @DisplayName("Deve listar todos os usuários")
    void listAll_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        var response = userService.listAll();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).email()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("Deve permitir reenviar o próprio email sem lançar exceção")
    void update_sameEmailAsOwn_doesNotThrow() {
        UserUpdateDTO dto = new UserUpdateDTO("João Silva", "joao@email.com"); // mesmo email do user do setUp()
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user)); // é ele mesmo
        when(userRepository.save(any(UserModel.class))).thenReturn(user);
        when(jwtService.generateToken(any(UserModel.class))).thenReturn("token");
        UserUpdateResponseDTO response = userService.update(1L, dto);
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Deve revogar o acesso do usuário depois de desativa-lo")
    void revoke_access_user_after_deactivate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deactivate(1L);
        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
        verify(refreshTokenService).revokeByUser(user);
    }


}
