package com.example.walletproject.controller;

import com.example.walletproject.dto.CreateTransactionRequest;
import com.example.walletproject.dto.TransactionResponse;
import com.example.walletproject.entity.Transaction;
import com.example.walletproject.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/accountId/{accountId}/deposit")
    public ResponseEntity<TransactionResponse> deposit(@PathVariable Long accountId, @RequestBody() CreateTransactionRequest body) {
        Transaction transaction = transactionService.deposit(accountId,body);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(transaction));
    }
    @PostMapping("/accountId/{accountId}/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@PathVariable Long accountId, @RequestBody() CreateTransactionRequest body) {
        Transaction transaction = transactionService.withdraw(accountId,body);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(transaction));
    }
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer( @RequestBody() CreateTransactionRequest body) {
        Transaction transaction = transactionService.transfer(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(transaction));
    }
}
