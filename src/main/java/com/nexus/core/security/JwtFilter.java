package com.nexus.core.security;

import com.nexus.core.exception.UserNotFoundException;
import com.nexus.core.user.UserModel;
import com.nexus.core.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        UserModel user = null;

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String email = jwtService.validateToken(token);
            if (email != null) {
                user = userRepository.findByEmail(email).orElse(null);
            }
        }
        if (user != null) {
            if (!user.isActive()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Esta conta foi desativada.\"}");
                return;
            }
            var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
            var auth = new UsernamePasswordAuthenticationToken(
                    user, null, List.of(authority)
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
