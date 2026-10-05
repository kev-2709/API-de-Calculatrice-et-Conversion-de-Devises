package com.example.calculator.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Une conversion effectuée et enregistrée en base.
 * Attention : "from" et "to" sont des mots réservés en SQL,
 * d'où les noms fromCurrency / toCurrency.
 */
@Entity
public class ConversionTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromCurrency;
    private String toCurrency;
    private double amount;
    private double rate;
    private double result;
    private LocalDateTime dateConversion;

    // Constructeur vide obligatoire pour JPA
    public ConversionTransaction() {}

    // Getters et setters (obligatoires pour la sérialisation JSON)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
    public double getResult() { return result; }
    public void setResult(double result) { this.result = result; }
    public LocalDateTime getDateConversion() { return dateConversion; }
    public void setDateConversion(LocalDateTime dateConversion) { this.dateConversion = dateConversion; }
}