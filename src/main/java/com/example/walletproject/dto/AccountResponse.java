package com.example.walletproject.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AccountResponse(long id, String ownerName, BigDecimal balance, LocalDateTime createdAt) {
}
