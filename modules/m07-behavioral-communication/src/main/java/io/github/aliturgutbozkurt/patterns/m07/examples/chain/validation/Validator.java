package io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A link of a validation chain. Two ways to link: {@link #and} runs every link and collects all errors,
 * {@link #andThen} stops at the first failure (fail fast).
 *
 * @param <T> type of the validated value
 * @see "m07 lesson, section Chain of Responsibility — validation chains"
 */
@FunctionalInterface
public interface Validator<T> {

    ValidationResult validate(T value);

    /** A single rule: valid when {@code isValid} holds, otherwise {@code error}. */
    static <T> Validator<T> rule(Predicate<? super T> isValid, String error) {
        Objects.requireNonNull(isValid, "isValid");
        Objects.requireNonNull(error, "error");
        return value -> isValid.test(value) ? new Valid() : new Invalid(List.of(error));
    }

    /** Collect all: always runs both validators and merges their errors. */
    default Validator<T> and(Validator<? super T> next) {
        Objects.requireNonNull(next, "next");
        return value -> ValidationResult.merge(validate(value), next.validate(value));
    }

    /** Fail fast: runs {@code next} only if this validator passed. */
    default Validator<T> andThen(Validator<? super T> next) {
        Objects.requireNonNull(next, "next");
        return value -> switch (validate(value)) {
            case Valid _ -> next.validate(value);
            case Invalid invalid -> invalid;
        };
    }
}
