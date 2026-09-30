package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.AccountId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Money;
import java.util.Objects;

/**
 * The input of the use case, already parsed: no strings, no HTTP, no CLI.
 *
 * @param from source account
 * @param to target account
 * @param amount amount to move
 * @see "m11 lesson, section Ports and Adapters"
 */
public record TransferCommand(AccountId from, AccountId to, Money amount) {

    public TransferCommand {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(amount, "amount");
    }
}
