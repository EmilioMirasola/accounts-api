package com.example.accounts.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Transaction {
    private final UUID transactionRef;
    private final long depositAccountId;
    private final long withdrawalAccountId;
    private final double amount;

    public Transaction(long depositAccountId, long withdrawalAccountId, double amount) {
        this.depositAccountId = depositAccountId;
        this.withdrawalAccountId = withdrawalAccountId;
        this.amount = amount;
        this.transactionRef = UUID.randomUUID();
    }
}
