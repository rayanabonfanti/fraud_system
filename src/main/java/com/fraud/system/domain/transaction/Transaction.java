package com.fraud.system.domain.transaction;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "transaction")
public class Transaction {
    @Id
    private String id;
    private String userId;
    private BigDecimal amount;
    private String location;

    public Transaction(String userId, BigDecimal amount, String location) {
        this.userId = userId;
        this.amount = amount;
        this.location = location;
    }

}