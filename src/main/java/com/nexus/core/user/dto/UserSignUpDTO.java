package com.nexus.core.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignUpDTO(
        @NotBlank
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
        String password) {

        public UserSignUpDTO {
                name = name !=null ? name.trim() : null;
                email = email != null ? email.trim().toLowerCase() : null;
        }
}
