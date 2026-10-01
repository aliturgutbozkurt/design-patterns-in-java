package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Account;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.AccountId;
import java.util.Optional;

/**
 * Outbound port owned by the core: the application says what it needs, an adapter decides how.
 *
 * @see "m11 lesson, section Ports and Adapters"
 */
@FunctionalInterface
public interface LoadAccountPort {

    Optional<Account> load(AccountId id);
}
