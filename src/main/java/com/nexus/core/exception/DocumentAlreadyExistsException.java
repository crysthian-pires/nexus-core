package com.nexus.core.exception;

public class DocumentAlreadyExistsException extends RuntimeException {
    public DocumentAlreadyExistsException(String document) {
        super("Documento já cadastrado: " + document);
    }
}
