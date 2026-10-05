package com.example.calculator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 * Réponse de l'API Frankfurter, par exemple :
 * {"amount":1.0,"base":"USD","date":"2026-09-29","rates":{"EUR":0.92}}
 * Seul le champ "rates" nous intéresse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TauxResponse(Map<String, Double> rates) {}