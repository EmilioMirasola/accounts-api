package com.example;

import com.example.accounts.application.commands.TransferMoneyCommand;
import com.example.accounts.application.commands.TransferMoneyCommandHandler;
import com.example.accounts.domain.Account;
import com.example.accounts.domain.AccountRepository;
import com.example.accounts.domain.TransactionRepository;
import com.example.common.errors.NotPersistedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static com.example.accounts.domain.Account.DEFAULT_INITIAL_BALANCE;

public class TransferMoneyCommandHandlerTest {
    @Mock
    AccountRepository accountRepository;
    private TransferMoneyCommandHandler handler;
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        var accountRepo = Mockito.mock(AccountRepository.class);
        accountRepository = accountRepo;

        var transactionRepo = Mockito.mock(TransactionRepository.class);
        transactionRepository = transactionRepo;

        handler = new TransferMoneyCommandHandler(accountRepo, transactionRepo);
    }

    @Test
    public void test_handle_whenFindingWithdrawalAccountFails_shouldThrowException() {
        //Arrange
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        var transferAmount = 500;

        Mockito.when(accountRepository.getByReference(sourceAccountId)).thenReturn(Optional.empty());
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(sourceAccountId, destinationAccountId, transferAmount);

        //Act
        Assertions.assertThrowsExactly(NotPersistedException.class, () -> this.handler.Handle(transferMoneyCommand));

        //Assert
    }

    @Test
    public void test_handle_whenFindingDepositAccountFails_shouldThrowException() {
        //Arrange
        Account withdrawalAccount = Account.createAccount("SourceAccount");
        var transferAmount = 500;

        Mockito.when(accountRepository.getByReference(Mockito.any()))
                .thenReturn(Optional.of(withdrawalAccount))
                .thenReturn(Optional.empty());

        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(UUID.randomUUID(), UUID.randomUUID(), transferAmount);

        //Act
        Assertions.assertThrowsExactly(NotPersistedException.class, () -> handler.Handle(transferMoneyCommand));

        //Assert
    }

    @Test
    public void test_handle_whenTransactionSuccessful_balancesChanged() {
        //Arrange
        double initialBalance = DEFAULT_INITIAL_BALANCE;
        long withdrawalAccountId = 1L;
        long depositAccountId = 2L;

        Account withdrawalAccount = Account.builder().name("withdrawalAccount").initialBalance(initialBalance).id(withdrawalAccountId).transactions(new ArrayList<>()).build();
        Account depositAccount = Account.builder().name("depositAccount").initialBalance(initialBalance).id(depositAccountId).transactions(new ArrayList<>()).build();

        double transferAmount = withdrawalAccount.getBalance() - 50;
        double withdrawalAccountBalanceBeforeTransaction = withdrawalAccount.getBalance();
        double depositAccountBalanceBeforeTransaction = depositAccount.getBalance();

        Mockito.when(accountRepository.getByReference(Mockito.any()))
                .thenReturn(Optional.of(withdrawalAccount))
                .thenReturn(Optional.of(depositAccount));

        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(UUID.randomUUID(), UUID.randomUUID(), transferAmount);

        //Act
        handler.Handle(transferMoneyCommand);

        //Assert
        double withdrawalAccountExpectedBalance = withdrawalAccountBalanceBeforeTransaction - transferAmount;
        double depositAccountAccountExpectedBalance = depositAccountBalanceBeforeTransaction + transferAmount;

        Assertions.assertEquals(withdrawalAccountExpectedBalance, withdrawalAccount.getBalance());
        Assertions.assertEquals(depositAccountAccountExpectedBalance, depositAccount.getBalance());
    }

    @Test
    public void test_handle_whenCreatingTransactionFails_shouldThrowException() {
        //Arrange
        double initialBalance = DEFAULT_INITIAL_BALANCE;
        long withdrawalAccountId = 1L;
        long depositAccountId = 2L;
        Account withdrawalAccount = Account.builder().name("withdrawalAccount").initialBalance(initialBalance).id(withdrawalAccountId).transactions(new ArrayList<>()).build();
        Account depositAccount = Account.builder().name("depositAccount").initialBalance(initialBalance).id(depositAccountId).transactions(new ArrayList<>()).build();

        var transferAmount = withdrawalAccount.getBalance() - withdrawalAccount.getBalance() / 2;

        Mockito.when(accountRepository.getByReference(Mockito.any()))
                .thenReturn(Optional.of(withdrawalAccount))
                .thenReturn(Optional.of(depositAccount));


        doThrow(new RuntimeException()).when(transactionRepository).create(Mockito.any());

        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(UUID.randomUUID(), UUID.randomUUID(), transferAmount);

        //Act
        Assertions.assertThrows(Exception.class, () -> handler.Handle(transferMoneyCommand));

        //Assert
    }
}
