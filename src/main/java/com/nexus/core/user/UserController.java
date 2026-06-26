package com.nexus.core.user;

import com.nexus.core.user.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Criar conta", description = "Cria uma nova conta de usuário")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserSignUpDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    @Operation(summary = "Listar usuários", description = "Lista todos os usuários (apenas ADMIN)")
    @SecurityRequirement(name = "Bearer Token")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listAllUsers() {
        return ResponseEntity.ok(userService.listAll());
    }

    @Operation(summary = "Buscar por ID", description = "Busca um usuário pelo ID")
    @SecurityRequirement(name = "Bearer Token")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Atualizar dados", description = "Atualiza nome e email do usuário")
    @SecurityRequirement(name = "Bearer Token")
    @PatchMapping("/{id}")
    public ResponseEntity<UserUpdateResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @Operation(summary = "Atualizar perfil", description = "Atualiza telefone e data de nascimento")
    @SecurityRequirement(name = "Bearer Token")
    @PatchMapping("/{id}/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserProfileUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateProfile(id, dto));
    }

    @Operation(summary = "Desativar usuário", description = "Desativa um usuário (apenas ADMIN)")
    @SecurityRequirement(name = "Bearer Token")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
