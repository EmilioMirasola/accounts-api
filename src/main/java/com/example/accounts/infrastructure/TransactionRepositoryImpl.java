package com.example.accounts.infrastructure;

import com.example.accounts.domain.TransactionRepository;
import com.example.accounts.domain.Transaction;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionRepositoryImpl implements TransactionRepository, PanacheRepository<TransactionDao> {

    @WithSpan("create transaction")
    @Override
    public void create(Transaction transaction) {
        TransactionDao transactionDao = TransactionDao.of(transaction);
        persist(transactionDao);
    }
}
