package com.example.calculator.controller;

import com.example.calculator.dto.ManualConversionRequest;
import com.example.calculator.dto.UpdateRateRequest;
import com.example.calculator.model.ConversionTransaction;
import com.example.calculator.model.ExchangeRate;
import com.example.calculator.service.CurrencyService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currency")
public class CurrencyConverterController {

    private final CurrencyService service;

    // Injection par constructeur (guide §5)
    public CurrencyConverterController(CurrencyService service) {
        this.service = service;
    }

    // GET /api/currency/convert?amount=100&from=USD&to=EUR
    @GetMapping("/convert")
    public ResponseEntity<ConversionTransaction> convertir(@RequestParam Double amount,
                                                           @RequestParam String from,
                                                           @RequestParam String to) {
        return ResponseEntity.ok(service.convertir(amount, from, to));
    }

    // POST /api/currency/convert  {"amount":100,"from":"USD","to":"XYZ","rate":2.5}
    @PostMapping("/convert")
    public ResponseEntity<ConversionTransaction> convertirTauxManuel(
            @RequestBody @Valid ManualConversionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.convertirAvecTauxManuel(req));
    }

    // PUT /api/currency/update-rate  {"from":"USD","to":"XYZ","rate":3.0}
    @PutMapping("/update-rate")
    public ResponseEntity<ExchangeRate> mettreAJourTaux(@RequestBody @Valid UpdateRateRequest req) {
        return ResponseEntity.ok(service.mettreAJourTaux(req));
    }

    // DELETE /api/currency/delete-transaction/5
    @DeleteMapping("/delete-transaction/{id}")
    public ResponseEntity<Void> supprimerTransaction(@PathVariable Long id) {
        service.supprimerTransaction(id);
        return ResponseEntity.noContent().build();
    }
}