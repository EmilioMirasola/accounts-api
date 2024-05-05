package com.example.accounts.domain;

import com.example.common.errors.ValidationException;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class Account {
    private Long id;
    private UUID reference;
    private String name;
    private double initialBalance;
    private List<Transaction> transactions;

    public static final double DEFAULT_INITIAL_BALANCE = 100;

    public static Account createAccount(String name) {
        if (name == null) {
            throw new ValidationException("Name of bank account cannot be null");
        }

        if (name.trim().isEmpty()) {
            throw new ValidationException("Name of bank account cannot be empty");
        }

        return Account
                .builder()
                .name(name)
                .initialBalance(DEFAULT_INITIAL_BALANCE)
                .reference(UUID.randomUUID())
                .transactions(new ArrayList<>())
                .build();
    }

    public Transaction transferMoney(AccountTransferAmount amount, Account depositAccount) {
        double balanceAfterWithdrawal = getBalance() - amount.value();
        if (balanceAfterWithdrawal < 0) {
            throw new ValidationException("Account balance cannot be negative");
        }

        Transaction transaction = new Transaction(depositAccount.id, this.id, amount.value());

        this.addTransaction(transaction);
        depositAccount.addTransaction(transaction);

        return transaction;
    }

    private void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public double getBalance() {
        List<Transaction> withdrawals = transactions.stream()
                .filter(transaction -> transaction.getWithdrawalAccountId() == id)
                .toList();

        List<Transaction> deposits = transactions.stream()
                .filter(transaction -> transaction.getDepositAccountId() == id)
                .toList();

        double withdrawalsSum = withdrawals.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        double depositsSum = deposits.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        return initialBalance - withdrawalsSum + depositsSum;
    }

    public List<Transaction> getOutgoingTransactions() {
        return transactions
                .stream()
                .filter(transaction -> transaction.getWithdrawalAccountId() == id)
                .toList();
    }

    public List<Transaction> getIncomingTransactions() {
        return transactions
                .stream()
                .filter(transaction -> transaction.getDepositAccountId() == id)
                .toList();
    }
}
