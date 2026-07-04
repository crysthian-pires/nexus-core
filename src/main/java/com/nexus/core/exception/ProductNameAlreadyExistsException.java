package com.nexus.core.exception;

public class ProductNameAlreadyExistsException extends RuntimeException {
    public ProductNameAlreadyExistsException(String name) {
        super("Produto já cadastrado: " + name);
    }
}
