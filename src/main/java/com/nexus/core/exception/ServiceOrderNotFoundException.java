package com.nexus.core.exception;

public class ServiceOrderNotFoundException extends RuntimeException {
    public ServiceOrderNotFoundException(Long id) {
        super("Ordem de serviço não encontrada com id: " + id);
    }
}
