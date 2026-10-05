package com.example.calculator.exception;

/**
 * API externe indisponible ou trop lente -> 503.
 * Hérite de RuntimeException : indispensable pour que @Transactional fasse le rollback.
 */
public class ExternalServiceUnavailableException extends RuntimeException {
    public ExternalServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}