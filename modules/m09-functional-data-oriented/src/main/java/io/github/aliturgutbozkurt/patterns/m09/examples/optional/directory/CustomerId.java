package io.github.aliturgutbozkurt.patterns.m09.examples.optional.directory;

import java.util.Objects;

/**
 * A customer number such as {@code C-1}.
 *
 * @see "m09 lesson, section Optional and Result — Optional as a return type"
 */
public record CustomerId(String value) {

    public CustomerId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String toString() {
        return value;
    }
}
