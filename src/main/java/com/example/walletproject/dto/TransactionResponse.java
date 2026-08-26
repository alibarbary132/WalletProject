package com.example.walletproject.dto;

import com.example.walletproject.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public record TransactionResponse(Long id, BigDecimal amount, String type, Long sourceAccount, Long destinationA
        , String status, LocalDateTime dateTime) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType().name(),
                Objects.nonNull(transaction.getSourceAccount())?transaction.getSourceAccount().getId() : null ,
                Objects.nonNull(transaction.getDestinationAccount())?transaction.getDestinationAccount().getId() : null,
                transaction.getStatus().name(), transaction.getCreatedAt());

    }

}
