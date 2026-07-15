package com.nexus.core.appointment.dto;

import com.nexus.core.appointment.AppointmentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppointmentUpdateDTO(

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String description,

        @Future(message = "O agendamento deve ser uma data futura")
        LocalDateTime scheduledAt,

        AppointmentStatus status,

        @DecimalMin(value = "0.0", inclusive = false, message = "O valor estimado deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "O valor estimado deve ter no máximo 10 dígitos inteiros e 2 decimais")
        BigDecimal estimatedValue,

        @Size(max = 1000, message = "As observações devem ter no máximo 1000 caracteres")
        String notes

) {
    public AppointmentUpdateDTO {
        description = description != null ? description.trim() : null;
    }
}
