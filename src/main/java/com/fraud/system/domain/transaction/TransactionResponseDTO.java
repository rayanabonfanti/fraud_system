package com.fraud.system.domain.transaction;

import java.math.BigDecimal;

public record TransactionResponseDTO(String userId, BigDecimal amount, String location) {
    public TransactionResponseDTO(Transaction transaction) {
        this(transaction.getUserId(), transaction.getAmount(), transaction.getLocation());
    }
}
