package com.fraud.system.repositories;

import com.fraud.system.domain.transaction.Transaction;
import com.fraud.system.domain.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByUserId(String userId);
}
