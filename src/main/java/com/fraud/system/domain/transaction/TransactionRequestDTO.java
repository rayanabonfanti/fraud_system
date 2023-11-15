package com.fraud.system.domain.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequestDTO(
        @NotNull
        String userId,

        @NotNull
        BigDecimal amount,

        @NotBlank
        String location

) {
}
