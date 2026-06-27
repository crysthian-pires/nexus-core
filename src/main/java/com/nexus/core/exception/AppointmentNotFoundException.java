package com.nexus.core.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(Long id) {
        super("Agendamento não encontrado com id: " + id);
    }
}
