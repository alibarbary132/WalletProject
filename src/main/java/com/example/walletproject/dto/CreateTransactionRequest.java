package com.example.walletproject.dto;

import com.example.walletproject.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTransactionRequest(BigDecimal amount, String type, Long sourceAccount, Long destinationAccount
        , LocalDateTime dateTime) {
}
