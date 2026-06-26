package com.nexus.core.user;

import com.nexus.core.exception.EmailAlreadyExistsException;
import com.nexus.core.exception.UserNotFoundException;
import com.nexus.core.security.JwtService;
import com.nexus.core.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponseDTO create(UserSignUpDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }
        UserModel user = new UserModel();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(Role.USER);
        return new UserResponseDTO(userRepository.save(user));
    }

    public List<UserResponseDTO> listAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    public UserResponseDTO findById(Long id) {
        return new UserResponseDTO(findUserById(id));
    }

    public UserUpdateResponseDTO update(Long id, UserUpdateDTO dto) {
        UserModel user = findUserById(id);
        if (dto.name() != null) user.setName(dto.name());
        if (dto.email() != null) {
            if (userRepository.existsByEmail(dto.email())) {
                throw new EmailAlreadyExistsException(dto.email());
            }
            user.setEmail(dto.email());
        }
        UserModel updated = userRepository.save(user);
        String newToken = jwtService.generateToken(updated);
        return new UserUpdateResponseDTO(new UserResponseDTO(updated), newToken);
    }

    public UserResponseDTO updateProfile(Long id, UserProfileUpdateDTO dto) {
        UserModel user = findUserById(id);
        if (dto.phone() != null) user.setPhone(dto.phone());
        if (dto.birthDate() != null) user.setBirthDate(dto.birthDate());
        return new UserResponseDTO(userRepository.save(user));
    }

    public void deactivate(Long id) {
        UserModel user = findUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    private UserModel findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
