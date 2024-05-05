package com.example.accounts.application.commands;

import com.example.accounts.domain.Account;
import com.example.accounts.domain.AccountRepository;
import com.example.accounts.domain.AccountTransferAmount;
import com.example.common.errors.NotPersistedException;
import com.example.accounts.domain.Transaction;
import com.example.accounts.domain.TransactionRepository;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.UUID;

@ApplicationScoped
@AllArgsConstructor
public class TransferMoneyCommandHandler {

    @Inject
    AccountRepository accountRepository;

    @Inject
    TransactionRepository transactionRepository;

    @Transactional
    @WithSpan("handle TransferMoneyCommand")
    public UUID Handle(TransferMoneyCommand command) {
        Account withdrawalAccount = accountRepository.getByReference(command.withdrawalAccountRef())
                .orElseThrow(() -> new NotPersistedException("could not find account with reference " + command.withdrawalAccountRef().toString() + " to withdraw money from"));

        Account depositAccount = accountRepository.getByReference(command.depositAccountRef())
                .orElseThrow(() -> new NotPersistedException("could not find account with reference " + command.withdrawalAccountRef().toString() + " to deposit money to"));

        AccountTransferAmount accountTransferAmount = new AccountTransferAmount(command.amount());

        Transaction transaction = withdrawalAccount.transferMoney(accountTransferAmount, depositAccount);

        transactionRepository.create(transaction);
        accountRepository.save(withdrawalAccount);
        accountRepository.save(depositAccount);

        return transaction.getTransactionRef();
    }
}
