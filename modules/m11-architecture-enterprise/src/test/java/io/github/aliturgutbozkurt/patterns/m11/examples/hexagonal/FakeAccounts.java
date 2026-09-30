package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.LoadAccountPort;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.SaveAccountPort;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Account;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.AccountId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Money;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Hand-written fake for both outbound ports, plus a spy on {@code save}: no adapter class on the test path. */
final class FakeAccounts implements LoadAccountPort, SaveAccountPort {

    private final Map<AccountId, Money> balances = new HashMap<>();
    final List<String> saved = new ArrayList<>();

    FakeAccounts with(String id, String balance) {
        balances.put(new AccountId(id), Money.of(balance));
        return this;
    }

    Money balanceOf(String id) {
        return balances.get(new AccountId(id));
    }

    @Override
    public Optional<Account> load(AccountId id) {
        return Optional.ofNullable(balances.get(id)).map(balance -> new Account(id, balance));
    }

    @Override
    public void save(Account account) {
        saved.add(account.id() + "=" + account.balance());
        balances.put(account.id(), account.balance());
    }
}
