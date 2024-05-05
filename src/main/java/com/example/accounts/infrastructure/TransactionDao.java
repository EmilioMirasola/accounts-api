package com.example.accounts.infrastructure;

import com.example.accounts.domain.Transaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "transactions")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TransactionDao {
    @Id
    @GeneratedValue
    private long id;

    private UUID ref;

    @Column(name = "withdrawal_account_id")
    private long withdrawalAccountId;

    @Column(name = "deposit_account_id")
    private long depositAccountId;

    private double amount;

    public static TransactionDao of(Transaction transaction) {
        return TransactionDao
                .builder()
                .ref(transaction.getTransactionRef())
                .withdrawalAccountId(transaction.getWithdrawalAccountId())
                .depositAccountId(transaction.getDepositAccountId())
                .amount(transaction.getAmount())
                .build();
    }

    public Transaction toDomain() {
        return Transaction
                .builder()
                .withdrawalAccountId(withdrawalAccountId)
                .depositAccountId(depositAccountId)
                .amount(amount)
                .transactionRef(ref)
                .build();
    }
}
