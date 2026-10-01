package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. Identity of an {@link Order}, such as {@code order-1}. */
public record OrderId(String value) {

    public OrderId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String toString() {
        return value;
    }
}
