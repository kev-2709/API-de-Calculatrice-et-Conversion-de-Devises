package com.example.calculator.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String operateur; // "addition", "soustraction", "multiplication", "division"
    private double a;
    private double b;
    private double resultat;
    private LocalDateTime date;
    // Constructeur vide obligatoire pour JPA
    public Operation() {}
    // Getters et setters (obligatoires pour que JSON fonctionne)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOperateur() { return operateur; }
    public void setOperateur(String operateur) { this.operateur = operateur; }
    public double getA() { return a; }
    public void setA(double a) { this.a = a; }
    public double getB() { return b; }
    public void setB(double b) { this.b = b; }
    public double getResultat() { return resultat; }
    public void setResultat(double resultat) { this.resultat = resultat; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}