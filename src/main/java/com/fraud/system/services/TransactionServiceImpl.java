package com.fraud.system.services;

import com.fraud.system.domain.transaction.Transaction;
import com.fraud.system.interfaces.TransactionService;
import com.fraud.system.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final Map<String, Map<BigDecimal, LocalDateTime>> lastTransactionTimeMap = new ConcurrentHashMap<>();
    @Autowired
    TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        transaction = transactionRepository.save(transaction);

        updateLastTransactionTime(transaction.getUserId(), transaction.getAmount());

        return transaction;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFraudulent(Transaction transaction) {
        String userId = transaction.getUserId();
        BigDecimal amount = transaction.getAmount();

        if (isRecentTransaction(userId, amount)) {
            return true;
        }

        List<Transaction> userTransactions = transactionRepository.findByUserId(userId);

        if (userTransactions.isEmpty()) {
            return transaction.getAmount().compareTo(BigDecimal.valueOf(1000.01)) >= 0;
        }

        BigDecimal sum = userTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageAmount = sum.divide(BigDecimal.valueOf(userTransactions.size()), 2, RoundingMode.HALF_UP);
        BigDecimal fraudDetectionLimit = averageAmount.multiply(BigDecimal.valueOf(1.5));

        return transaction.getAmount().compareTo(fraudDetectionLimit) > 0
                || transaction.getAmount().compareTo(BigDecimal.valueOf(1000.01)) >= 0;
    }


    private boolean isRecentTransaction(String userId, BigDecimal amount) {
        Map<BigDecimal, LocalDateTime> userTransactionTimeMap = lastTransactionTimeMap.get(userId);

        if (userTransactionTimeMap != null) {
            LocalDateTime lastTransactionTime = userTransactionTimeMap.get(amount);

            if (lastTransactionTime != null) {
                Duration duration = Duration.between(lastTransactionTime, LocalDateTime.now());
                return duration.getSeconds() < 600;
            }
        }

        return false;
    }

    private void updateLastTransactionTime(String userId, BigDecimal amount) {
        lastTransactionTimeMap
                .computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                .put(amount, LocalDateTime.now());
    }

    public Map<String, Map<BigDecimal, LocalDateTime>> getLastTransactionTimeMap() {
        return lastTransactionTimeMap;
    }

}
