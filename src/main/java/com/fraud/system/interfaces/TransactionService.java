package com.fraud.system.interfaces;

import com.fraud.system.domain.transaction.Transaction;

public interface TransactionService {
    Transaction saveTransaction(Transaction transaction);
    boolean isFraudulent(Transaction transaction);
}
