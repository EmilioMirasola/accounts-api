package com.example.accounts.infrastructure;

import com.example.accounts.domain.Account;
import com.example.accounts.domain.AccountRepository;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AccountRepositoryImpl implements AccountRepository, PanacheRepository<AccountDao> {
    @Inject
    EntityManager entityManager;

    @WithSpan("create account")
    @Override
    public void create(Account account) {
        var dao = AccountDao.of(account);
        dao.setInitialBalance(account.getBalance());
        persist(dao);
    }

    @WithSpan("get accounts")
    @Override
    public List<Account> getAccounts(int page, int size) {

        List<AccountDao> daos = entityManager.createQuery(
                "SELECT a " +
                        "from AccountDao a" +
                        " LEFT JOIN FETCH a.outgoingTransactions" +
                        " LEFT JOIN FETCH a.incomingTransactions", AccountDao.class
        ).getResultList();

        return daos.stream().map(AccountDao::toDomain).toList();
    }

    @Override
    @WithSpan("get account by ref")
    public Optional<Account> getByReference(UUID accountRef) {
        try {
            AccountDao dao = entityManager.createQuery(
                            "SELECT a " +
                                    "from AccountDao a" +
                                    " LEFT JOIN FETCH a.outgoingTransactions" +
                                    " LEFT JOIN FETCH a.incomingTransactions " +
                                    "WHERE a.ref = :ref", AccountDao.class)
                    .setParameter("ref", accountRef)
                    .getSingleResult();

            return Optional.of(dao.toDomain());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @WithSpan("save account")
    public void save(Account account) {
        AccountDao accountDao = AccountDao.of(account);
        accountDao = entityManager.merge(accountDao);
        persist(accountDao);
    }
}
