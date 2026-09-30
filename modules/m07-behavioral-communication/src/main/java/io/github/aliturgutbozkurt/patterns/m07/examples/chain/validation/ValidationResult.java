package io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation;

import java.util.stream.Stream;

/**
 * Outcome of a {@link Validator}: {@link Valid} or {@link Invalid} with the error messages.
 *
 * @see "m07 lesson, section Chain of Responsibility — validation chains"
 */
public sealed interface ValidationResult permits Valid, Invalid {

    default boolean isValid() {
        return this instanceof Valid;
    }

    /** Both results together: valid only if both are, otherwise every error of the first, then of the second. */
    static ValidationResult merge(ValidationResult first, ValidationResult second) {
        return switch (first) {
            case Valid _ -> second;
            case Invalid(var errors) -> switch (second) {
                case Valid _ -> first;
                case Invalid(var more) -> new Invalid(Stream.concat(errors.stream(), more.stream()).toList());
            };
        };
    }
}
