package com.example.calculator.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limitation de débit : 20 requêtes par minute et par adresse IP,
 * uniquement sur GET /api/currency/convert (guide §13, version par IP avec remise à zéro).
 * Au-delà : 429 Too Many Requests.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int LIMITE = 20;               // requêtes
    private static final long FENETRE_MS = 60_000;      // par minute

    /** Fenêtre de comptage d'une adresse IP. */
    private static class Fenetre {
        long debut;
        int compteur;
        Fenetre(long debut) { this.debut = debut; }
    }

    private final Map<String, Fenetre> fenetres = new ConcurrentHashMap<>();

    // Le filtre ne s'applique qu'à GET /api/currency/convert
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("GET".equals(request.getMethod())
                && "/api/currency/convert".equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (!autoriser(request.getRemoteAddr())) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Retry-After", "60");
            // Même format JSON que GlobalExceptionHandler (le filtre s'exécute avant les contrôleurs)
            response.getWriter().write(String.format(
                    "{\"timestamp\":\"%s\",\"status\":429,\"error\":\"Too Many Requests\","
                            + "\"message\":\"Limite de %d requêtes par minute dépassée\",\"path\":\"%s\"}",
                    LocalDateTime.now(), LIMITE, request.getRequestURI()));
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean autoriser(String ip) {
        long maintenant = System.currentTimeMillis();
        Fenetre f = fenetres.computeIfAbsent(ip, k -> new Fenetre(maintenant));
        synchronized (f) {
            if (maintenant - f.debut >= FENETRE_MS) { // nouvelle minute : remise à zéro
                f.debut = maintenant;
                f.compteur = 0;
            }
            f.compteur++;
            return f.compteur <= LIMITE;
        }
    }
}