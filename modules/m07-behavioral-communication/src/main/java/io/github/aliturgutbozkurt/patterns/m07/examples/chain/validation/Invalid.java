package io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation;

import java.util.List;

/**
 * The value broke at least one rule; {@code errors} is immutable and in chain order.
 *
 * @see "m07 lesson, section Chain of Responsibility — validation chains"
 */
public record Invalid(List<String> errors) implements ValidationResult {

    public Invalid {
        errors = List.copyOf(errors);
        if (errors.isEmpty()) {
            throw new IllegalArgumentException("an Invalid result needs at least one error");
        }
    }
}
