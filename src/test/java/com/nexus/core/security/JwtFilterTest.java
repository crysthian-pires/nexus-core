package com.nexus.core.security;

import com.nexus.core.user.UserModel;
import com.nexus.core.user.UserRepository;
import com.nexus.core.user.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    private UserModel user;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        user = new UserModel();
        user.setId(1L);
        user.setEmail("usuario@email.com");
        user.setRole(Role.USER);
    }

    @Test
    @DisplayName("Deve barrar requisição com 401 se o usuário do token estiver desativado")
    void shouldReturn401WhenUserIsInactive() throws Exception {
        user.setActive(false);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        when(request.getHeader("Authorization")).thenReturn("Bearer token_valido");
        when(jwtService.validateToken("token_valido")).thenReturn("usuario@email.com");
        when(userRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(user));
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        assertThat(stringWriter.toString()).contains("Esta conta foi desativada.");
        verify(filterChain, never()).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Deve permitir requisição e autenticar se o usuário estiver ativo")
    void shouldAllowRequestWhenUserIsActive() throws Exception {
        user.setActive(true);
        when(request.getHeader("Authorization")).thenReturn("Bearer token_valido");
        when(jwtService.validateToken("token_valido")).thenReturn("usuario@email.com");
        when(userRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(user));
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);
    }
}
