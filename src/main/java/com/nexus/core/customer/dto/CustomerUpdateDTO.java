package com.nexus.core.customer.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerUpdateDTO(

        @Size(max = 200, message = "O nome deve ter no máximo 200 caracteres")
        String name,

        @Email(message = "E-mail inválido")
        @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres")
        String email,

        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$",
                message = "Telefone deve estar no formato E.164 (Ex: +5511999998888)")
        String phone,

        @Pattern(regexp = "^\\d{11}$|^\\d{14}$",
                message = "Documento deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos)")
        String document,

        @Size(max = 1000, message = "As observações devem ter no máximo 1000 caracteres")
        String notes

) {
    public CustomerUpdateDTO {
        name = name != null ? name.trim() : null;
        email = email != null ? email.trim().toLowerCase() : null;
        document = document != null ? normalizeDocument(document) : null;
    }

    private static String normalizeDocument(String raw) {
        String digitsOnly = raw.replaceAll("\\D", "");
        return digitsOnly.isEmpty() ? null : digitsOnly;
    }

    @AssertTrue(message = "O nome não pode ser vazio ou conter apenas espaços")
    private boolean isNameValid() {
        return name == null || !name.isBlank();
    }
}
