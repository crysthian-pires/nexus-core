package com.nexus.core.security;

import com.nexus.core.user.UserRepository;
import com.nexus.core.user.dto.UserLoginDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "Login", description = "Autentica o usuário e retorna access token e refresh token")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO dto) {
        return userRepository.findByEmail(dto.email())
                .filter(user -> passwordEncoder.matches(dto.password(), user.getPassword()))
                .map(user -> {
                    String accessToken = jwtService.generateToken(user);
                    String refreshToken = refreshTokenService.generate(user).getToken();
                    return ResponseEntity.ok(Map.of(
                            "accessToken", accessToken,
                            "refreshToken", refreshToken
                    ));
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @Operation(summary = "Refresh token", description = "Gera um novo access token usando o refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        RefreshTokenModel token = refreshTokenService.validate(refreshToken);
        String newAccessToken = jwtService.generateToken(token.getUser());
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @Operation(summary = "Logout", description = "Revoga o refresh token do usuário")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        RefreshTokenModel token = refreshTokenService.validate(refreshToken);
        refreshTokenService.revokeByUser(token.getUser());
        return ResponseEntity.noContent().build();
    }
}
