package com.example.calculator.repository;

import com.example.calculator.model.ConversionTransaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversionTransactionRepository extends JpaRepository<ConversionTransaction, Long> {
    // save(), findById(), existsById(), deleteById()... sont fournis par JpaRepository
}