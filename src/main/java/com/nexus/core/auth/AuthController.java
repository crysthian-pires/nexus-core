package com.nexus.core.auth;

import com.nexus.core.auth.dto.AuthResponseDTO;
import com.nexus.core.auth.dto.UserLoginDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login, refresh token e logout")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Autentica o usuário e retorna access token e refresh token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody UserLoginDTO dto) {
        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh token", description = "Gera um novo access token usando o refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody Map<String, String> body) {
        AuthResponseDTO response = authService.refresh(body.get("refreshToken"));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout", description = "Revoga o refresh token do usuário")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
        authService.logout(body.get("refreshToken"));
        return ResponseEntity.noContent().build();
    }
}
