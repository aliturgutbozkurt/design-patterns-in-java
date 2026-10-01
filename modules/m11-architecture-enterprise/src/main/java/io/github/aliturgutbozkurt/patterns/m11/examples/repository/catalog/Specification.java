package io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog;

import java.util.Objects;

/**
 * Specification: a query as a composable object, instead of one {@code findBy…} method per combination.
 *
 * @param <T> the type of the candidates
 * @see "m11 lesson, section Repository — Specification"
 */
@FunctionalInterface
public interface Specification<T> {

    /** Whether {@code candidate} matches. */
    boolean isSatisfiedBy(T candidate);

    /** Both this and {@code other}. */
    default Specification<T> and(Specification<T> other) {
        Objects.requireNonNull(other, "other");
        return candidate -> isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate);
    }

    /** This or {@code other} (or both). */
    default Specification<T> or(Specification<T> other) {
        Objects.requireNonNull(other, "other");
        return candidate -> isSatisfiedBy(candidate) || other.isSatisfiedBy(candidate);
    }

    /** Everything this specification does not match. */
    default Specification<T> not() {
        return candidate -> !isSatisfiedBy(candidate);
    }
}
