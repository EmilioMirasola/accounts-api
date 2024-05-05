package com.example.accounts.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    void create(Account account);

    List<Account> getAccounts(int page, int size);

    Optional<Account> getByReference(UUID accountRef);

    void save(Account withdrawalAccount);
}
