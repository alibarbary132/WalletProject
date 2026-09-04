package com.example.walletproject.service;

import com.example.walletproject.dto.CreateTransactionRequest;
import com.example.walletproject.entity.Account;
import com.example.walletproject.entity.Transaction;
import com.example.walletproject.exception.AccountNotFoundException;
import com.example.walletproject.exception.InsufficientBalanceException;

import com.example.walletproject.respository.AccountRepo;
import com.example.walletproject.respository.TransactionRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// No Spring context needed here — pure Mockito unit test. Fast, no DB, no server startup.
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepo accountRepository;

    @Mock
    private TransactionRepo transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account("Test User");
        testAccount.setBalance(new BigDecimal("100.00"));
        ReflectionTestUtils.setField(testAccount, "id", 1L);   // manually assign the ID, test-only

    }

    @Test
    void withdraw_withSufficientBalance_succeedsAndReducesBalance() {
        when(accountService.getAccountById(1L)).thenReturn(testAccount);
        // Arrange
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));



        // Act
         Transaction result = transactionService.withdraw(testAccount.getId(), new CreateTransactionRequest(new BigDecimal(50),Transaction.Type.WITHDRAWAL.name(),null,null,null));


        // Assert
        assertThat(result.getStatus()).isEqualTo(Transaction.Status.COMPLETED);
        assertThat(testAccount.getBalance()).isEqualByComparingTo("50.00");
        assertThat(result.getSourceAccount().getOwnerName()).isEqualTo(testAccount.getOwnerName());
        verify(transactionRepository).save(result);
    }

//    @Test
//    void withdraw_withInsufficientBalance_throwsAndDoesNotSave() {
//        when(transactionRepository.findByIdempotencyKey("key-2")).thenReturn(Optional.empty());
//        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
//
//        assertThatThrownBy(() ->
//                transactionService.withdraw(1L, new BigDecimal("500.00"), "key-2")
//        ).isInstanceOf(InsufficientFundsException.class);
//
//        // Critical: confirm nothing was saved when the withdrawal is rejected
//        verify(transactionRepository, never()).save(any());
//        verify(accountRepository, never()).save(any());
//    }
//
//    @Test
//    void withdraw_withDuplicateIdempotencyKey_returnsExistingTransactionWithoutReprocessing() {
//        Transaction existing = new Transaction(
//                Transaction.Type.WITHDRAWAL, new BigDecimal("30.00"), testAccount, null, "key-3");
//        when(transactionRepository.findByIdempotencyKey("key-3")).thenReturn(Optional.of(existing));
//
//        Transaction result = transactionService.withdraw(1L, new BigDecimal("30.00"), "key-3");
//
//        assertThat(result).isSameAs(existing);
//        // Confirm the account was never even looked up — proves it short-circuited
//        verify(accountRepository, never()).findById(any());
//    }
//
//    @Test
//    void withdraw_withNonExistentAccount_throwsAccountNotFoundException() {
//        when(transactionRepository.findByIdempotencyKey("key-4")).thenReturn(Optional.empty());
//        when(accountRepository.findById(999L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() ->
//                transactionService.withdraw(999L, new BigDecimal("10.00"), "key-4")
//        ).isInstanceOf(AccountNotFoundException.class);
//    }
//
//    @Test
//    void withdraw_withNegativeAmount_throwsInvalidAmountException() {
//        assertThatThrownBy(() ->
//                transactionService.withdraw(1L, new BigDecimal("-10.00"), "key-5")
//        ).isInstanceOf(InvalidAmountException.class);
//
//        // Confirm validation happens before any repository call at all
//        verifyNoInteractions(accountRepository, transactionRepository);
//    }
}