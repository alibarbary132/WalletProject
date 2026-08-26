package com.example.walletproject.service;

import com.example.walletproject.dto.CreateAccountRequest;
import com.example.walletproject.entity.Account;
import com.example.walletproject.exception.AccountNotFoundException;
import com.example.walletproject.respository.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepo accountRepo;

    public AccountService(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Transactional
    public Account getAccountById(Long accountId) {
        return accountRepo.findById(accountId).orElseThrow(()->new AccountNotFoundException(accountId));
    }

    @Transactional
    public BigDecimal getBalanceById(Long accountId) {
        Account account = accountRepo.findById(accountId).orElseThrow(()->new AccountNotFoundException(accountId));
        return account.getBalance();
    }

    @Transactional
    public List<Account> getAllAccounts() {
        return accountRepo.findAll();
    }

    @Transactional
    public Account  createAccount(CreateAccountRequest createAccountRequest) {
        return accountRepo.save(new Account(createAccountRequest.ownerName()));
    }

    @Transactional
    public Account  updateAccount(Account account) {
        return accountRepo.save(account);
    }
    @Transactional
    public List<Account>  updateAccounts(List<Account> accounts) {
        return accountRepo.saveAll(accounts);
    }

    @Transactional
    public void deleteAccount(Account account) {
        accountRepo.delete(account);
    }
}
