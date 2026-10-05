package com.example.calculator.exception;

public class OperationNotFoundException extends RuntimeException {
    public OperationNotFoundException(Long id) {
        super("Opération introuvable avec l'id " + id);
    }
}