package com.example.calculator.service;

import com.example.calculator.dto.OperationRequest;
import com.example.calculator.exception.DivisionByZeroException;
import com.example.calculator.exception.OperationNotFoundException;
import com.example.calculator.model.Operation;
import com.example.calculator.repository.OperationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CalculatorService {

    private final OperationRepository repository;

    public CalculatorService(OperationRepository repository) {
        this.repository = repository;
    }

    public Operation calculer(OperationRequest req) {
        Operation op = new Operation();
        appliquer(op, req);
        op.setDate(LocalDateTime.now());
        return repository.save(op);
    }

    public Page<Operation> historique(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Operation modifier(Long id, OperationRequest req) {
        Operation op = repository.findById(id)
                .orElseThrow(() -> new OperationNotFoundException(id));
        appliquer(op, req);   // recalcule le résultat
        return repository.save(op);
    }

    public void supprimer(Long id) {
        if (!repository.existsById(id)) {
            throw new OperationNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // Calcule le résultat et remplit l'entité (utilisé par POST et PUT)
    private void appliquer(Operation op, OperationRequest req) {
        String operateur = req.getOperation().trim().toLowerCase();
        double a = req.getA();
        double b = req.getB();

        double resultat = switch (operateur) {
            case "addition" -> a + b;
            case "soustraction" -> a - b;
            case "multiplication" -> a * b;
            case "division" -> {
                if (b == 0) {
                    throw new DivisionByZeroException();
                }
                yield a / b;
            }
            default -> throw new IllegalArgumentException(
                    "Opération inconnue : " + req.getOperation()
                            + " (valeurs acceptées : addition, soustraction, multiplication, division)");
        };

        op.setOperateur(operateur);
        op.setA(a);
        op.setB(b);
        op.setResultat(resultat);
    }
}