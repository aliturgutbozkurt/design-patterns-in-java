package io.github.aliturgutbozkurt.patterns.capstone.api.model;

import java.util.Objects;

/**
 * GIVEN — do not modify. Identifies a customer; also the recipient of the customer's notifications.
 *
 * @param value a non-blank id such as {@code alice}
 * @see "capstone brief, Business rules — Money and ids"
 */
public record CustomerId(String value) {

    public CustomerId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("blank customer id");
        }
    }
}
