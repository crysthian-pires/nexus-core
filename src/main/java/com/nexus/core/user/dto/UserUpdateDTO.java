package com.nexus.core.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(

        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String name,

        @Email(message = "E-mail inválido")
        @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres")
        String email

) {
        public UserUpdateDTO {
                name = name != null ? name.trim() : null;
                email = email != null ? email.trim().toLowerCase() : null;
        }

        @AssertTrue(message = "O nome não pode ser vazio ou conter apenas espaços")
        private boolean isNameValid(){
                return name == null || !name.isBlank();
        }


}
