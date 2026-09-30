package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain;

import java.util.Objects;

/**
 * Identity of an {@link Order}, such as {@code order-1}.
 *
 * @param value not blank
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public record OrderId(String value) {

    public OrderId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("blank order id");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
