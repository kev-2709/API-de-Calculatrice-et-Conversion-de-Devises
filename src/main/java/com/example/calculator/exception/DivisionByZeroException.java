package com.example.calculator.exception;

public class DivisionByZeroException extends RuntimeException {
    public DivisionByZeroException() {
        super("La division par zéro n'est pas autorisée");
    }
}