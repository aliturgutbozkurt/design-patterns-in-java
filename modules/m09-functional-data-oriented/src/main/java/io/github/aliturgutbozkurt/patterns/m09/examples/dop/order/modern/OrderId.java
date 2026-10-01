package io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern;

import java.util.Objects;

/**
 * An order number as its own type, so it cannot be mixed up with any other {@code String}.
 *
 * @see "m09 lesson, section Data-oriented programming — Modern Java 27"
 */
public record OrderId(String value) {

    public OrderId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("order id must not be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
