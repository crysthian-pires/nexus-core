package com.nexus.core.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(

        @NotBlank(message = "O nome é obrigatório")
        String name,

        @Email(message = "E-mail inválido")
        String email

) {
}
