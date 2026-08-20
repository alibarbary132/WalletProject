package com.example.walletproject.service;

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
    public Account getAccount(Long accountId) {
        return accountRepo.findById(accountId).orElseThrow(()->new AccountNotFoundException(accountId));
    }

    @Transactional
    public BigDecimal getBalanceById(Long accountId) {
        Account account = accountRepo.findById(accountId).orElseThrow(()->new AccountNotFoundException(accountId));
        return account.getBalance();
    }

    @Transactional
    public List<Account> getAllAccounts(Long accountId) {
        return accountRepo.findAll();
    }

    @Transactional
    public void  createAccount(Account account) {
        accountRepo.save(account);
    }

    @Transactional
    public void deleteAccount(Account account) {
        accountRepo.delete(account);
    }
}
