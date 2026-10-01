package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain;

import java.util.Objects;

/**
 * Identity of an {@link Account}, such as {@code A-1}.
 *
 * @param value not blank
 * @see "m11 lesson, section Ports and Adapters"
 */
public record AccountId(String value) {

    public AccountId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("blank account id");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
