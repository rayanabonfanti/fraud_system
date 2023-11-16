package com.fraud.system.controllers;

import com.fraud.system.domain.transaction.Transaction;
import com.fraud.system.interfaces.TransactionService;
import com.fraud.system.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    public void healthcheck_ReturnsOk() {
        ResponseEntity<String> response = transactionController.healthcheck();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK", response.getBody());
    }

    @Test
    public void createTransaction_NotFraudulent_ReturnsOk() {
        Transaction transaction = new Transaction();
        when(transactionService.isFraudulent(transaction)).thenReturn(false);

        ResponseEntity<String> response = transactionController.createTransaction(transaction);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transaction saved successfully.", response.getBody());

        verify(transactionService, times(1)).saveTransaction(eq(transaction));
    }

    @Test
    public void createTransaction_Fraudulent_ReturnsBadRequest() {
        Transaction transaction = new Transaction();
        when(transactionService.isFraudulent(transaction)).thenReturn(true);

        ResponseEntity<String> response = transactionController.createTransaction(transaction);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Transaction is considered fraudulent.", response.getBody());

        verify(transactionService, never()).saveTransaction(any(Transaction.class));
    }
}
