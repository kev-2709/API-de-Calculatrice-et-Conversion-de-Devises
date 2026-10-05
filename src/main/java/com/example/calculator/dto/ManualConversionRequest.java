package com.example.calculator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Corps JSON de POST /api/currency/convert (taux manuel). */
public record ManualConversionRequest(
        @NotNull(message = "Le champ amount est obligatoire")
        @Positive(message = "Le montant doit être strictement positif")
        Double amount,

        @NotBlank(message = "La devise source (from) est obligatoire")
        @Size(min = 3, max = 3, message = "Le code de devise doit contenir 3 lettres")
        String from,

        @NotBlank(message = "La devise cible (to) est obligatoire")
        @Size(min = 3, max = 3, message = "Le code de devise doit contenir 3 lettres")
        String to,

        @NotNull(message = "Le champ rate est obligatoire")
        @Positive(message = "Le taux doit être strictement positif")
        Double rate) {}