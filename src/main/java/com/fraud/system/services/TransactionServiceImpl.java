package com.fraud.system.services;

import com.fraud.system.domain.transaction.Transaction;
import com.fraud.system.interfaces.TransactionService;
import com.fraud.system.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.math.RoundingMode;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    // Mapa para armazenar o último momento em que uma transação foi feita por userId e valor
    private final Map<String, Map<BigDecimal, LocalDateTime>> lastTransactionTimeMap = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        // Salva a transação no MongoDB
        transaction = transactionRepository.save(transaction);

        // Atualiza o mapa com o último momento da transação
        updateLastTransactionTime(transaction.getUserId(), transaction.getAmount());

        return transaction;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFraudulent(Transaction transaction) {
        String userId = transaction.getUserId();
        BigDecimal amount = transaction.getAmount();

        // Verifica se houve uma transação semelhante recentemente
        if (isRecentTransaction(userId, amount)) {
            return true; // Transação considerada fraudulenta
        }

        // Recupera todas as transações anteriores do usuário no MongoDB
        List<Transaction> userTransactions = transactionRepository.findByUserId(userId);

        if (userTransactions != null && !userTransactions.isEmpty()) {
            // Calcula a média das transações anteriores
            BigDecimal sum = BigDecimal.ZERO;
            for (Transaction userTransaction : userTransactions) {
                sum = sum.add(userTransaction.getAmount());
            }
            BigDecimal averageAmount = sum.divide(BigDecimal.valueOf(userTransactions.size()), 2, RoundingMode.HALF_UP);

            // Define um limite para considerar uma transação como fraudulenta (por exemplo, 1.5 vezes a média)
            BigDecimal fraudDetectionLimit = averageAmount.multiply(BigDecimal.valueOf(1.5));

            // Compara o valor da transação com o limite de detecção de fraude
            return transaction.getAmount().compareTo(fraudDetectionLimit) > 0;
        } else {
            // Se for a primeira transação, permitir até 1000
            return transaction.getAmount().compareTo(BigDecimal.valueOf(1000.01)) >= 0;
        }
    }

    // Método para verificar se houve uma transação semelhante recentemente
    private boolean isRecentTransaction(String userId, BigDecimal amount) {
        Map<BigDecimal, LocalDateTime> userTransactionTimeMap = lastTransactionTimeMap.get(userId);

        if (userTransactionTimeMap != null) {
            LocalDateTime lastTransactionTime = userTransactionTimeMap.get(amount);

            if (lastTransactionTime != null) {
                // Verifica se o tempo desde a última transação é menor que um certo limite (por exemplo, 1 minuto)
                Duration duration = Duration.between(lastTransactionTime, LocalDateTime.now());
                return duration.getSeconds() < 60; // Alterar para Período de 10 minutos (600 segundos)
            }
        }

        return false;
    }

    // Método para atualizar o mapa com o último momento da transação
    private void updateLastTransactionTime(String userId, BigDecimal amount) {
        lastTransactionTimeMap
                .computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                .put(amount, LocalDateTime.now());
    }

}
