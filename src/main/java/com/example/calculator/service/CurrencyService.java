package com.example.calculator.service;

import com.example.calculator.client.ExchangeRateClient;
import com.example.calculator.dto.ManualConversionRequest;
import com.example.calculator.dto.UpdateRateRequest;
import com.example.calculator.exception.ResourceNotFoundException;
import com.example.calculator.model.ConversionTransaction;
import com.example.calculator.model.ExchangeRate;
import com.example.calculator.repository.ConversionTransactionRepository;
import com.example.calculator.repository.ExchangeRateRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/** Logique de conversion + persistance (guide §11). */
@Service
public class CurrencyService {

    private static final Pattern CODE_DEVISE = Pattern.compile("^[A-Z]{3}$");

    private final ExchangeRateClient client;
    private final ConversionTransactionRepository transactionRepository;
    private final ExchangeRateRepository rateRepository;

    public CurrencyService(ExchangeRateClient client,
                           ConversionTransactionRepository transactionRepository,
                           ExchangeRateRepository rateRepository) {
        this.client = client;
        this.transactionRepository = transactionRepository;
        this.rateRepository = rateRepository;
    }

    /**
     * Conversion avec le taux de l'API externe.
     * @Transactional : si l'appel externe ou le calcul échoue (exception non contrôlée),
     * rien n'est enregistré en base (rollback).
     */
    @Transactional
    public ConversionTransaction convertir(Double amount, String from, String to) {
        validerMontant(amount);
        String f = normaliser(from);
        String t = normaliser(to);

        double taux;
        if (f.equals(t)) {
            taux = 1.0;
        } else {
            try {
                taux = client.getTaux(f, t); // appel externe (cache + retry)
            } catch (IllegalArgumentException e) {
                // Devise non couverte par l'API externe : repli sur un taux manuel stocké localement
                taux = rateRepository.findByFromCurrencyAndToCurrency(f, t)
                        .map(ExchangeRate::getRate)
                        .orElseThrow(() -> e);
            }
        }
        return enregistrer(f, t, amount, taux);
    }

    /** Conversion avec un taux fourni manuellement ; le taux est aussi mémorisé localement. */
    @Transactional
    public ConversionTransaction convertirAvecTauxManuel(ManualConversionRequest req) {
        String f = normaliser(req.from());
        String t = normaliser(req.to());

        ExchangeRate rate = rateRepository.findByFromCurrencyAndToCurrency(f, t)
                .orElseGet(ExchangeRate::new);
        rate.setFromCurrency(f);
        rate.setToCurrency(t);
        rate.setRate(req.rate());
        rateRepository.save(rate);

        return enregistrer(f, t, req.amount(), req.rate());
    }

    @Transactional
    public ExchangeRate mettreAJourTaux(UpdateRateRequest req) {
        String f = normaliser(req.from());
        String t = normaliser(req.to());

        ExchangeRate rate = rateRepository.findByFromCurrencyAndToCurrency(f, t)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun taux local pour " + f + " -> " + t));
        rate.setRate(req.rate());
        return rateRepository.save(rate);
    }

    @Transactional
    public void supprimerTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction " + id + " introuvable");
        }
        transactionRepository.deleteById(id);
    }

    private ConversionTransaction enregistrer(String from, String to, double amount, double taux) {
        ConversionTransaction tx = new ConversionTransaction();
        tx.setFromCurrency(from);
        tx.setToCurrency(to);
        tx.setAmount(amount);
        tx.setRate(taux);
        tx.setResult(amount * taux);
        tx.setDateConversion(LocalDateTime.now());
        return transactionRepository.save(tx);
    }

    private void validerMontant(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }
    }

    private String normaliser(String code) {
        String c = (code == null) ? "" : code.trim().toUpperCase();
        if (!CODE_DEVISE.matcher(c).matches()) {
            throw new IllegalArgumentException(
                    "Code de devise invalide : '" + code + "' (3 lettres attendues, ex. EUR)");
        }
        return c;
    }
}