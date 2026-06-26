package com.nexus.core.user.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserProfileUpdateDTO(

        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$",
                message = "Telefone deve estar no formato E.164 (Ex: +5511999998888)")
        String phone,

        @Past(message = "A data de nascimento deve ser uma data no passado")
        LocalDate birthDate

) {
}
