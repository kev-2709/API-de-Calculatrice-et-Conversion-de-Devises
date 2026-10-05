package com.example.calculator.model;

import jakarta.persistence.*;

/** Taux de change stocké localement (taux manuels, devises non couvertes par l'API externe). */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"from_currency", "to_currency"}))
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromCurrency;
    private String toCurrency;
    private double rate;

    public ExchangeRate() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
}