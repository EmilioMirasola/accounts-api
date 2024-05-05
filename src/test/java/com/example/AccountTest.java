package com.example;

import com.example.accounts.domain.Account;
import com.example.accounts.domain.AccountTransferAmount;
import com.example.common.errors.ValidationException;
import com.example.accounts.domain.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.example.accounts.domain.Account.DEFAULT_INITIAL_BALANCE;

public class AccountTest {

    @Test
    void testCreateAccount_whenNameIsNull_shouldThrowException() {
        //Arrange
        String accountName = null;

        //Act
        Assertions.assertThrowsExactly(ValidationException.class, () -> Account.createAccount(accountName));

        //Assert
    }

    @Test
    void testCreateAccount_whenNameIsEmpty_shouldThrowException() {
        //Arrange
        var accountName = "";

        //Act
        Assertions.assertThrowsExactly(ValidationException.class, () -> Account.createAccount(accountName));

        //Assert
    }

    @Test
    void testCreateAccount_whenNameIsNotNullAndNotBlank_shouldCreateAccount() {
        //Arrange
        var accountName = "Lønkonto";
        //Act
        Assertions.assertDoesNotThrow(() -> Account.createAccount(accountName));

        //Assert
    }

    @Test
    public void testGetBalance_noTransactions_shouldReturnInitialBalance() {
        //Arrange
        Account account = Account.createAccount("account");

        //Act

        //Assert
        Assertions.assertEquals(DEFAULT_INITIAL_BALANCE, account.getBalance());
    }

    @Test
    public void testGetBalance_withTransactions_shouldReturnDepositMinusWithdrawalPlusInitialBalance() {
        //Arrange
        double initialBalance = DEFAULT_INITIAL_BALANCE;
        double amount = 50;
        long withdrawalAccountId = 1L;
        long depositAccountId = 2L;

        Transaction transaction = new Transaction(depositAccountId, withdrawalAccountId, amount);

        Account withdrawalAccount = Account.builder().name("withdrawalAccount").initialBalance(initialBalance).id(withdrawalAccountId).transactions(List.of(transaction)).build();
        Account depositAccount = Account.builder().name("depositAccount").initialBalance(initialBalance).id(depositAccountId).transactions(List.of(transaction)).build();

        //Act

        //Assert
        double expectedWithdrawalAccountBalance = initialBalance - amount;
        double expectedDepositAccountBalance = initialBalance + amount;

        Assertions.assertEquals(expectedWithdrawalAccountBalance, withdrawalAccount.getBalance());
        Assertions.assertEquals(expectedDepositAccountBalance, depositAccount.getBalance());
    }

    @Test
    public void testTransferMoney_insufficientFunds_shouldThrowException() {
        //Arrange
        Account withdrawalAccount = Account.builder().name("withdrawalAccount").initialBalance(DEFAULT_INITIAL_BALANCE).id(1L).transactions(List.of()).build();
        Account depositAccount = Account.builder().name("depositAccount").initialBalance(DEFAULT_INITIAL_BALANCE).id(2L).transactions(List.of()).build();

        //Act
        Assertions.assertThrowsExactly(ValidationException.class, () -> withdrawalAccount.transferMoney(new AccountTransferAmount(200), depositAccount));

        //Assert
        Assertions.assertEquals(DEFAULT_INITIAL_BALANCE, depositAccount.getBalance());
    }

    @Test
    public void testTransferMoney_enoughFunds_shouldSucceed() {
        //Arrange
        Account withdrawalAccount = Account.builder().name("withdrawalAccount").initialBalance(DEFAULT_INITIAL_BALANCE).id(1L).transactions(new ArrayList<>()).build();
        Account depositAccount = Account.builder().name("depositAccount").initialBalance(DEFAULT_INITIAL_BALANCE).id(2L).transactions(new ArrayList<>()).build();

        //Act
        Assertions.assertDoesNotThrow(() -> withdrawalAccount.transferMoney(new AccountTransferAmount(100), depositAccount));

        //Assert
    }

    @Test
    public void testTransferMoney_enoughFunds_transactionAddedToAccounts() {
        //Arrange
        Account withdrawalAccount = Account.builder().name("withdrawalAccount").initialBalance(DEFAULT_INITIAL_BALANCE).id(1L).transactions(new ArrayList<>()).build();
        Account depositAccount = Account.builder().name("depositAccount").initialBalance(DEFAULT_INITIAL_BALANCE).id(2L).transactions(new ArrayList<>()).build();

        //Act
        withdrawalAccount.transferMoney(new AccountTransferAmount(100), depositAccount);

        //Assert
        Assertions.assertEquals(1, withdrawalAccount.getTransactions().size());
        Assertions.assertEquals(1, depositAccount.getTransactions().size());
    }

    @Test
    public void testTransferMoney_whenWithdrawalAccountHasInsufficientBalance_shouldThrowException() {
        //Arrange
        Account withdrawalAccount = Account.createAccount("withdrawalAccount");
        Account depositAccount = Account.createAccount("depositAccount");
        var amount = new AccountTransferAmount(withdrawalAccount.getBalance() + 1);

        //Act
        Assertions.assertThrowsExactly(ValidationException.class, () -> withdrawalAccount.transferMoney(amount, depositAccount));

        //Assert
    }

    @Test
    public void testTransferMoney_whenWithdrawalAccountHasInsufficientBalance_moneyShouldNotBeDepositToOtherAccount() {
        //Arrange
        Account withdrawalAccount = Account.createAccount("withdrawalAccount");
        Account depositAccount = Account.createAccount("depositAccount");
        var withdrawalAmount = new AccountTransferAmount(withdrawalAccount.getBalance() + 1);
        var depositAccountBalanceBeforeTransactionAttempt = depositAccount.getBalance();

        //Act
        try {
            withdrawalAccount.transferMoney(withdrawalAmount, depositAccount);
            Assertions.fail("Expected an exception as more withdraw account balance is too low");
        } catch (ValidationException ignored) {
            //Assert
            Assertions.assertEquals(depositAccountBalanceBeforeTransactionAttempt, depositAccount.getBalance());
        }
    }
}
