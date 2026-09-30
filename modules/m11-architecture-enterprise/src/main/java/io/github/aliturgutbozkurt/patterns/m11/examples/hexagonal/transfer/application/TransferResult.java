package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.AccountId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Money;
import java.util.Objects;

/**
 * Every business outcome of a transfer, as a closed set: callers {@code switch} over it exhaustively.
 *
 * @see "m11 lesson, section Ports and Adapters"
 */
public sealed interface TransferResult {

    /** The money moved. */
    record Transferred(AccountId from, AccountId to, Money amount) implements TransferResult {}

    /** The transfer was refused for a business reason; nothing changed. */
    record Rejected(String reason) implements TransferResult {
        public Rejected {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
