package com.example.calculator.repository;

import com.example.calculator.model.ExchangeRate;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    // Spring génère la requête à partir du nom de la méthode
    Optional<ExchangeRate> findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
}