package com.nexus.core.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginDTO(

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(max=72, message = "Senha inválida")
        String password
) {
        public UserLoginDTO {
                email = email != null ? email.trim().toLowerCase() : null;
        }
}
