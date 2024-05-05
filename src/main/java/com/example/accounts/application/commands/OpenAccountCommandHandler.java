package com.example.accounts.application.commands;

import com.example.accounts.domain.Account;
import com.example.accounts.domain.AccountRepository;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class OpenAccountCommandHandler {

    @Inject
    AccountRepository accountRepository;

    @Transactional
    @WithSpan("handle NewAccountCommand")
    public UUID Handle(OpenAccountCommand command) {
        Account account = Account.createAccount(command.AccountName());
        accountRepository.create(account);
        return account.getReference();
    }
}
