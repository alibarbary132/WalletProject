package com.example.walletproject.dto;

import lombok.Builder;

import java.math.BigDecimal;
@Builder

public record BalanceResponse(Long accountId, String ownerName , BigDecimal balance) {
}
