package com.example.accounts.infrastructure;

import com.example.accounts.domain.Account;
import com.example.accounts.domain.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Table(name = "accounts")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountDao {
    @Id
    @GeneratedValue
    private Long id;

    private UUID ref;

    private String name;

    private double initialBalance;

    @OneToMany(mappedBy = "withdrawalAccountId")
    private Set<TransactionDao> outgoingTransactions = new HashSet<>();

    @OneToMany(mappedBy = "depositAccountId")
    private Set<TransactionDao> incomingTransactions = new HashSet<>();

    public static AccountDao of(Account account) {
        Set<TransactionDao> outgoingTransactions = account.getOutgoingTransactions()
                .stream()
                .map(TransactionDao::of)
                .collect(Collectors.toSet());

        Set<TransactionDao> incomingTransactions = account.getIncomingTransactions()
                .stream()
                .map(TransactionDao::of)
                .collect(Collectors.toSet());

        return new AccountDao(account.getId(), account.getReference(), account.getName(), account.getInitialBalance(), outgoingTransactions, incomingTransactions);
    }

    public Account toDomain() {
        List<Transaction> outgoingTransactions = this.outgoingTransactions.stream()
                .map(TransactionDao::toDomain)
                .toList();

        List<Transaction> incomingTransactions = this.incomingTransactions.stream()
                .map(TransactionDao::toDomain)
                .toList();

        List<Transaction> allTransactions = Stream.concat(
                outgoingTransactions.stream(),
                incomingTransactions.stream()
        ).collect(Collectors.toList());

        return Account
                .builder()
                .id(id)
                .name(name)
                .reference(ref)
                .initialBalance(initialBalance)
                .transactions(allTransactions)
                .build();
    }
}
