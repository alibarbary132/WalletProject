package com.example.walletproject.service;

import com.example.walletproject.dto.CreateAccountRequest;
import com.example.walletproject.entity.Account;
import com.example.walletproject.exception.AccountNotFoundException;
import com.example.walletproject.respository.AccountRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.assertj.core.api.Assertions.assertThat;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepo accountRepository;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        // Manual construction here instead of @InjectMocks — just to show the alternative style.
        // Both are equally valid; @InjectMocks does this same thing automatically.
        accountService = new AccountService(accountRepository);
    }

    @Test
    void createAccount_withValidOwnerName_savesAndReturnsAccount() {
        // Arrange
        Account savedAccount = new Account("Ali");
        when(accountRepository.save(org.mockito.ArgumentMatchers.any(Account.class)))
                .thenReturn(savedAccount);

        // Act
        Account result = accountService.createAccount(new CreateAccountRequest("Ali"));

        // Assert
        assertThat(result.getOwnerName()).isEqualTo("Ali");
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(accountRepository).save(org.mockito.ArgumentMatchers.any(Account.class));
    }

    @Test
    void getAccount_withExistingId_returnsAccount() {
        Account savedAccount = new Account("Ali");
        when(accountRepository.findById(savedAccount.getId())).thenReturn(Optional.of(savedAccount));
        Account result = accountService.getAccountById(savedAccount.getId());
        assertThat(result).isEqualTo(savedAccount);

    }

    @Test
    void getAccount_withNonExistentId_throwsAccountNotFoundException() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accountService.getAccountById(999L)).isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getBalance_withExistingAccount_returnsCorrectBalance() {
        Account account = new Account("Ali");
        account.setBalance(new BigDecimal("250.50"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        BigDecimal balance = accountService.getBalanceById(1L);

        assertThat(balance).isEqualByComparingTo("250.50");
    }
}