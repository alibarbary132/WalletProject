package com.example.walletproject.controller;

import com.example.walletproject.dto.AccountResponse;
import com.example.walletproject.dto.BalanceResponse;
import com.example.walletproject.dto.CreateAccountRequest;
import com.example.walletproject.entity.Account;
import com.example.walletproject.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    @GetMapping("/{id}/balance")
    public BalanceResponse getBalanceById(@PathVariable Long id) {
        BigDecimal balance = accountService.getBalanceById(id);
        return BalanceResponse.builder().balance(balance).accountId(id).build();
    }
    @GetMapping("/{id}")
    public AccountResponse getAccountById(@PathVariable Long id) {
        Account account  = accountService.getAccountById(id);
        return AccountResponse.builder().balance(account.getBalance()).id(id).ownerName(account.getOwnerName())
                .createdAt(account.getCreatedAt()).build();
    }
    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest createAccountRequest) {
        Account createdAccount = accountService.createAccount(createAccountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                AccountResponse.builder().id(createdAccount.getId())
                        .balance(createdAccount.getBalance())
                        .ownerName(createdAccount.getOwnerName())
                        .createdAt(createdAccount.getCreatedAt()).build());


    }
}
