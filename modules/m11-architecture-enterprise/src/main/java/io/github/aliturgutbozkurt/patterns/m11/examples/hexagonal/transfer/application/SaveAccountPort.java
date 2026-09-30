package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Account;

/**
 * Outbound port owned by the core, kept separate from {@link LoadAccountPort} so each use case asks for exactly what
 * it uses (Interface Segregation).
 *
 * @see "m11 lesson, section Ports and Adapters"
 */
@FunctionalInterface
public interface SaveAccountPort {

    void save(Account account);
}
