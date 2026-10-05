package com.example.calculator.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OperationRequest {

    @NotNull(message = "Le champ a est obligatoire")
    @DecimalMin(value = "-1000000", message = "a doit être supérieur ou égal à -1000000")
    @DecimalMax(value = "1000000", message = "a doit être inférieur ou égal à 1000000")
    private Double a;

    @NotNull(message = "Le champ b est obligatoire")
    @DecimalMin(value = "-1000000", message = "b doit être supérieur ou égal à -1000000")
    @DecimalMax(value = "1000000", message = "b doit être inférieur ou égal à 1000000")
    private Double b;

    @NotBlank(message = "L'opération est obligatoire")
    private String operation;

    public Double getA() { return a; }
    public void setA(Double a) { this.a = a; }

    public Double getB() { return b; }
    public void setB(Double b) { this.b = b; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
}