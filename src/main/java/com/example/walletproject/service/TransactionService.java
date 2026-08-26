package com.example.walletproject.service;

import com.example.walletproject.dto.CreateTransactionRequest;
import com.example.walletproject.entity.Account;
import com.example.walletproject.entity.Transaction;
import com.example.walletproject.exception.InsufficientBalanceException;
import com.example.walletproject.respository.TransactionRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepo transactionRepo;
    private final AccountService accountService;
    public TransactionService(TransactionRepo transactionRepo, AccountService accountService) {
        this.transactionRepo = transactionRepo;
        this.accountService = accountService;
    }
    @Transactional
    public Transaction createTransaction(CreateTransactionRequest createTransactionRequest) {
        Account sourceAccount = accountService.getAccountById(createTransactionRequest.sourceAccount());
        Account destinationAccount = accountService.getAccountById(createTransactionRequest.destinationAccount());
        return transactionRepo.save(new Transaction(Transaction.Type.valueOf(createTransactionRequest.type())
        ,createTransactionRequest.amount(),sourceAccount,destinationAccount)

        );
    }
    @Transactional
    public Transaction deposit(long accountId,CreateTransactionRequest createTransactionRequest) {
        Account destinationAccount = accountService.getAccountById(accountId);
        destinationAccount.setBalance(destinationAccount.getBalance().add(createTransactionRequest.amount()));
        Transaction transaction = new Transaction(Transaction.Type.DEPOSIT
                ,createTransactionRequest.amount(),null,destinationAccount);
        transaction.setStatus(Transaction.Status.COMPLETED);
        accountService.updateAccount(destinationAccount);
        return transactionRepo.save(transaction);
    }
    @Transactional
    public Transaction withdraw(long accountId,CreateTransactionRequest createTransactionRequest) {
        Account sourceAccount = accountService.getAccountById(accountId);
        if(sourceAccount.getBalance().compareTo(createTransactionRequest.amount())<1){
            throw new InsufficientBalanceException(sourceAccount.getId(),createTransactionRequest.amount(),sourceAccount.getBalance());
        }
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(createTransactionRequest.amount()));
        Transaction transaction = new Transaction(Transaction.Type.WITHDRAWAL
                ,createTransactionRequest.amount(),sourceAccount,null);
        transaction.setStatus(Transaction.Status.COMPLETED);
        accountService.updateAccount(sourceAccount);
        return transactionRepo.save(transaction);
    }
    public Transaction transfer(CreateTransactionRequest createTransactionRequest) {
        Account sourceAccount = accountService.getAccountById(createTransactionRequest.sourceAccount());
        if(sourceAccount.getBalance().compareTo(createTransactionRequest.amount())<1){
            throw new InsufficientBalanceException(sourceAccount.getId(),createTransactionRequest.amount(),sourceAccount.getBalance());
        }
        Account destinationAccount = accountService.getAccountById(createTransactionRequest.destinationAccount());
        destinationAccount.setBalance(destinationAccount.getBalance().add(createTransactionRequest.amount()));
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(createTransactionRequest.amount()));
        Transaction transaction = new Transaction(Transaction.Type.TRANSFER
                ,createTransactionRequest.amount(),sourceAccount,destinationAccount);
        transaction.setStatus(Transaction.Status.COMPLETED);
        accountService.updateAccounts(Arrays.asList(sourceAccount,destinationAccount));
        return transactionRepo.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

}
