package com.example.calculator.client;

import com.example.calculator.dto.TauxResponse;
import com.example.calculator.exception.ExternalServiceUnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * Appel de l'API externe de taux de change (guide §9, §10, §12).
 *
 * Ce composant est VOLONTAIREMENT séparé de CurrencyService : @Cacheable et @Retryable
 * passent par un proxy Spring, qui est ignoré si la méthode est appelée depuis
 * la même classe.
 */
@Component
public class ExchangeRateClient {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateClient.class);

    private final WebClient webClient;
    private final Duration timeout;

    public ExchangeRateClient(@Value("${currency.api.base-url}") String baseUrl,
                              @Value("${currency.api.timeout-seconds:3}") long timeoutSeconds) {
        this.webClient = WebClient.create(baseUrl);
        this.timeout = Duration.ofSeconds(timeoutSeconds);
    }

    /**
     * - @Cacheable : le taux d'un couple (from, to) n'est demandé qu'une fois à l'API externe.
     *   Les exceptions ne sont jamais mises en cache.
     * - @Retryable (Spring Framework 7) : 3 appels au total, 1 s d'attente entre chaque,
     *   uniquement en cas d'indisponibilité.
     *   Une devise inconnue (IllegalArgumentException) n'est PAS rejouée.
     */
    @Cacheable("taux")
    @Retryable(includes = ExternalServiceUnavailableException.class,
            maxRetries = 2,   // 1 appel initial + 2 nouvelles tentatives = 3 appels au total
            delay = 1000)     // 1 s d'attente entre chaque tentative (en millisecondes)
    public Double getTaux(String from, String to) {
        log.info("Appel de l'API externe : {} -> {}", from, to);
        try {
            TauxResponse reponse = webClient.get()
                    .uri("/latest?from={from}&to={to}", from, to)
                    .retrieve()
                    .bodyToMono(TauxResponse.class)
                    .block(timeout); // synchrone, avec délai maximal

            if (reponse == null || reponse.rates() == null || reponse.rates().get(to) == null) {
                throw new IllegalArgumentException("Taux introuvable pour " + from + " -> " + to);
            }
            return reponse.rates().get(to);

        } catch (WebClientResponseException e) {
            int code = e.getStatusCode().value();
            if (e.getStatusCode().is4xxClientError() && code != 429) {
                // 404 / 422... : devise inconnue de l'API -> erreur du client, pas de retry
                throw new IllegalArgumentException(
                        "Devise non supportée par l'API externe : " + from + " -> " + to);
            }
            // 5xx ou 429 : problème côté service externe -> retry puis 503
            throw new ExternalServiceUnavailableException(
                    "API de taux de change indisponible (HTTP " + code + ")", e);

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            // Connexion refusée, DNS, délai dépassé...
            log.warn("Échec de l'appel externe : {}", e.toString());
            throw new ExternalServiceUnavailableException(
                    "API de taux de change injoignable ou trop lente", e);
        }
    }
}