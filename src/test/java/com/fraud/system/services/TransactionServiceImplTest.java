package com.fraud.system.services;

import com.fraud.system.domain.transaction.Transaction;
import com.fraud.system.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceImplTest {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveTransaction_SuccessfulSave_ReturnsTransaction() {
        Transaction transaction = new Transaction();
        transaction.setUserId("testUser");
        transaction.setAmount(BigDecimal.TEN);

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction savedTransaction = transactionService.saveTransaction(transaction);

        assertNotNull(savedTransaction);
        assertEquals(transaction.getUserId(), savedTransaction.getUserId());
        assertEquals(transaction.getAmount(), savedTransaction.getAmount());

        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void isFraudulent_NotRecentTransactionAboveLimit_ReturnsTrue() {
        String userId = "testUser";
        BigDecimal amount = BigDecimal.valueOf(1500);

        List<Transaction> userTransactions = Collections.singletonList(new Transaction(userId, BigDecimal.valueOf(1000), "Local A"));
        when(transactionRepository.findByUserId(userId)).thenReturn(userTransactions);

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);

        boolean isFraudulent = transactionService.isFraudulent(transaction);

        assertFalse(isFraudulent);
    }


    @Test
    void isFraudulent_NotRecentTransactionBelowLimit_ReturnsFalse() {
        String userId = "testUser";
        BigDecimal amount = BigDecimal.valueOf(800);

        List<Transaction> userTransactions = Collections.singletonList(new Transaction(userId, BigDecimal.valueOf(700), "Local A"));
        when(transactionRepository.findByUserId(userId)).thenReturn(userTransactions);

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(amount);

        boolean isFraudulent = transactionService.isFraudulent(transaction);

        assertFalse(isFraudulent);
    }

}
