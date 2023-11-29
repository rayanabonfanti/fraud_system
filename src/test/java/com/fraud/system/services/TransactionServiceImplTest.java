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
import java.util.concurrent.ConcurrentHashMap;

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
        BigDecimal amount = BigDecimal.valueOf(100);

        List<Transaction> userTransactions = Collections.singletonList(new Transaction(userId, BigDecimal.valueOf(100), "Local A"));
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

    @Test
    void isRecentTransaction_ReturnsTrue() {
        String userId = "testUser";
        BigDecimal amount = BigDecimal.valueOf(1500);

        Map<BigDecimal, LocalDateTime> recentTransactions = new ConcurrentHashMap<>();
        recentTransactions.put(amount, LocalDateTime.now().minusSeconds(300));

        transactionService.getLastTransactionTimeMap().put(userId, recentTransactions);

        boolean result = transactionService.isRecentTransaction(userId, amount);
        assertTrue(result);
    }

    @Test
    void isRecentTransaction_ReturnsTrueII() {
        String userId = "testUser";
        BigDecimal amount = BigDecimal.valueOf(1200);

        Map<BigDecimal, LocalDateTime> recentTransactions = new ConcurrentHashMap<>();
        recentTransactions.put(amount, LocalDateTime.now().minusSeconds(300));

        transactionService.getLastTransactionTimeMap().put(userId, recentTransactions);

        boolean result = transactionService.isFraudulent(new Transaction(userId, amount, "Local A"));

        assertTrue(result);
    }

    @Test
    void isEmptyUserTransactions_ReturnsTrue() {
        String userId = "testUser";
        BigDecimal amount = BigDecimal.valueOf(1200);

        when(transactionRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        boolean result = transactionService.isFraudulent(new Transaction(userId, amount, "Local A"));

        assertTrue(result);
    }

    @Test
    void isRecentTransaction_ReturnsTrueIII() {
        String userId = "testUser";
        BigDecimal amount = BigDecimal.valueOf(1200); // Ajuste conforme necessário

        // Configurar o serviço para ter uma transação recente com uma diferença maior que 600 segundos
        Map<BigDecimal, LocalDateTime> recentTransactions = new ConcurrentHashMap<>();
        recentTransactions.put(amount, LocalDateTime.now().minusSeconds(700)); // Mais de 600 segundos atrás

        transactionService.getLastTransactionTimeMap().put(userId, recentTransactions);

        // Chamar o método isFraudulent
        boolean result = transactionService.isFraudulent(new Transaction(userId, amount, "Local A"));

        // Verificar se o método retorna true
        assertTrue(result);
    }

}
