package com.example.calculator.repository;

import com.example.calculator.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
    // findAll(Pageable) est déjà fourni par JpaRepository :
    // inutile de le redéclarer.
}