package com.nexus.core.auth;

import com.nexus.core.auth.exception.DisabledUserException;
import com.nexus.core.auth.dto.AuthResponseDTO;
import com.nexus.core.auth.dto.UserLoginDTO;
import com.nexus.core.security.JwtService;
import com.nexus.core.security.RefreshTokenService;
import com.nexus.core.user.UserModel;
import com.nexus.core.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;


    public AuthResponseDTO login(UserLoginDTO dto){
        UserModel user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        if (!user.isActive()){
            throw new DisabledUserException("Esta Conta foi desativada. Contacte o suporte");
        }
        if(!passwordEncoder.matches(dto.password(), user.getPassword())){
            throw new BadCredentialsException("Senha Incorreta");
        }
        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.generate(user);
        return new AuthResponseDTO(accessToken, refreshToken);
    }

    public AuthResponseDTO refresh(String refreshToken) {
        UserModel user = refreshTokenService.validate(refreshToken).getUser();
        String newAccessToken = jwtService.generateToken(user);
        return new AuthResponseDTO(newAccessToken, refreshToken);
    }

    public void logout(String refreshToken){
        refreshTokenService.revokeByUser(
                refreshTokenService.validate(refreshToken)
                        .getUser());

    }

}
