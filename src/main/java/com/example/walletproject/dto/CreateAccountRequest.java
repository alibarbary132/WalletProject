package com.example.walletproject.dto;

import lombok.Builder;

@Builder
public record CreateAccountRequest(String ownerName) {
}
