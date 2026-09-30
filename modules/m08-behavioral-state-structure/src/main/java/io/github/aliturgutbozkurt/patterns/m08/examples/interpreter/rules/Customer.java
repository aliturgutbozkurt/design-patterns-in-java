package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.rules;

import java.util.Objects;
import java.util.Set;

/**
 * Interpreter's context: the facts a rule is evaluated against.
 *
 * @see "m08 lesson, section Interpreter — Classic Java"
 */
public record Customer(String id, int age, String country, long spentCents, Set<String> tags) {

    public Customer {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(country, "country");
        if (age < 0) {
            throw new IllegalArgumentException("age must not be negative: " + age);
        }
        tags = Set.copyOf(tags);
    }
}
