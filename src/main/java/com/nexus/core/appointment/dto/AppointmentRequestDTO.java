package com.nexus.core.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppointmentRequestDTO(

        @NotNull(message = "O cliente é obrigatório")
        Long customerId,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "A data e hora são obrigatórias")
        @Future(message = "O agendamento deve ser uma data futura")
        LocalDateTime scheduledAt,

        BigDecimal estimatedValue,
        String notes

) {}
