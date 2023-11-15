package com.fraud.system.controllers;

import com.fraud.system.domain.transaction.Transaction;
import com.fraud.system.domain.transaction.TransactionRequestDTO;
import com.fraud.system.domain.transaction.TransactionResponseDTO;
import com.fraud.system.interfaces.TransactionService;
import com.fraud.system.repositories.TransactionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("transaction")
public class TransactionController {

    @Autowired
    TransactionRepository repository;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity healthcheck() {
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping
    public ResponseEntity<String> createTransaction(@RequestBody Transaction transaction) {
        if (transactionService.isFraudulent(transaction)) {
            return new ResponseEntity<>("Transaction is considered fraudulent.", HttpStatus.BAD_REQUEST);
        }

        transactionService.saveTransaction(transaction);
        return new ResponseEntity<>("Transaction saved successfully.", HttpStatus.OK);
    }

}
