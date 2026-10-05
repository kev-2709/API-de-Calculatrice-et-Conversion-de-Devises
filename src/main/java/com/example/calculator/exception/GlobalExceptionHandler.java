package com.example.calculator.exception;

import com.example.calculator.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Contraintes @NotNull, @DecimalMin... non respectées
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    // Division par zéro et opération inconnue
    @ExceptionHandler({DivisionByZeroException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex,
                                                          HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Ressource introuvable
    @ExceptionHandler(OperationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(OperationNotFoundException ex,
                                                        HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
                                                HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                status.value(), status.getReasonPhrase(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    // 400 : paramètre d'URL manquant (ex. ?amount= absent)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex,
                                                                  HttpServletRequest req) {
        return buildBody(HttpStatus.BAD_REQUEST, "Paramètre obligatoire manquant : " + ex.getParameterName(), req);
    }

    // 400 : paramètre du mauvais type (ex. amount=abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return buildBody(HttpStatus.BAD_REQUEST, "Valeur invalide pour le paramètre : " + ex.getName(), req);
    }

    // 400 : corps JSON illisible
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return buildBody(HttpStatus.BAD_REQUEST, "Corps de requête JSON invalide ou illisible", req);
    }

    // 404 : transaction / taux introuvable
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return buildBody(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    // 503 : API externe indisponible après les 3 tentatives de retry
    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleExternalUnavailable(ExternalServiceUnavailableException ex, HttpServletRequest req) {
        return buildBody(HttpStatus.SERVICE_UNAVAILABLE,
                "Service de taux de change indisponible, veuillez réessayer plus tard", req);
    }

    // Même format JSON que le sujet : timestamp, status, error, message, path
    private ResponseEntity<Map<String, Object>> buildBody(HttpStatus status, String message,
                                                          HttpServletRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}