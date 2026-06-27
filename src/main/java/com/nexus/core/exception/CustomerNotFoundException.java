package com.nexus.core.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("Cliente não encontrado com id: " + id);
    }
}
