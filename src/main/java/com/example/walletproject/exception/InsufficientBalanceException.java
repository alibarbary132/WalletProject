package com.example.walletproject.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(long accountId, BigDecimal amount,BigDecimal balance)
    {
        super("insufficient balance for account " + accountId + " with amount " + amount + " and balance " + balance);
    }
}
