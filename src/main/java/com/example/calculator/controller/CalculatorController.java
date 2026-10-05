package com.example.calculator.controller;

import com.example.calculator.dto.OperationRequest;
import com.example.calculator.model.Operation;
import com.example.calculator.service.CalculatorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Operation> calculer(@RequestBody @Valid OperationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.calculer(req));
    }

    // GET /api/calculator/history?page=0&size=10&sort=date,desc
    @GetMapping("/history")
    public Page<Operation> historique(Pageable pageable) {
        return service.historique(pageable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Operation> modifier(@PathVariable Long id,
                                              @RequestBody @Valid OperationRequest req) {
        return ResponseEntity.ok(service.modifier(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        service.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}