package com.example.accounts.application.queries;

import com.example.accounts.application.dto.AccountDTO;
import com.example.accounts.domain.Account;
import com.example.accounts.domain.AccountRepository;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FindAccountsQueryHandler {

    @Inject
    AccountRepository accountRepository;

    @Transactional
    @WithSpan("handle FindAccountsQuery")
    public List<AccountDTO> handle(FindAccountsQuery query) {
        List<Account> accounts = accountRepository.getAccounts(query.page(), query.numberOfAccounts());
        return accounts.stream().map(account -> new AccountDTO(account.getName(), account.getReference().toString(), account.getBalance())).toList();
    }
}
