package com.nexus.core.exception;

public class InvalidFinalizationException extends RuntimeException {
    public InvalidFinalizationException() {
        super("Não é possível finalizar a OS sem um valor total maior que zero");
    }
}
