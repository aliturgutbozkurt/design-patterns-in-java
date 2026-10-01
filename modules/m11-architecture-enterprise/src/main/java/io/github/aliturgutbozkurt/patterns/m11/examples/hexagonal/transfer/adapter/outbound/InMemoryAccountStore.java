package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.adapter.outbound;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.LoadAccountPort;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.SaveAccountPort;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Account;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.AccountId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Money;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;

/**
 * Outbound adapter implementing both persistence ports with a map. It stores balances, not {@link Account} objects,
 * so every {@code load} returns a fresh entity — like a database would.
 *
 * @see "m11 lesson, section Ports and Adapters"
 */
public final class InMemoryAccountStore implements LoadAccountPort, SaveAccountPort {

    private final SequencedMap<AccountId, Money> balances = new LinkedHashMap<>();

    /** Opens an account (set-up data for the demo). */
    public void open(AccountId id, Money balance) {
        balances.put(Objects.requireNonNull(id, "id"), Objects.requireNonNull(balance, "balance"));
    }

    @Override
    public Optional<Account> load(AccountId id) {
        return Optional.ofNullable(balances.get(id)).map(balance -> new Account(id, balance));
    }

    @Override
    public void save(Account account) {
        balances.put(account.id(), account.balance());
    }

    /** Every balance in opening order (an unmodifiable copy). */
    public SequencedMap<AccountId, Money> balances() {
        return Collections.unmodifiableSequencedMap(new LinkedHashMap<>(balances));
    }
}
