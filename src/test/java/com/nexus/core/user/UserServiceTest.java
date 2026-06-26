package com.nexus.core.user;

import com.nexus.core.exception.EmailAlreadyExistsException;
import com.nexus.core.exception.UserNotFoundException;
import com.nexus.core.security.JwtService;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        when(userRepository.existsByEmail("novo@email.com")).thenReturn(false);
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

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existente@email.com")).thenReturn(true);

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
}
