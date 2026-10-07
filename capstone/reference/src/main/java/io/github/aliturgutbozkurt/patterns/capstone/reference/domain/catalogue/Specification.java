package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.Objects;

/**
 * A query criterion as a composable object (adapted from modules/m11-…/repository/catalog/Specification.java).
 *
 * @param <T> the type of the candidates
 * @see "capstone guide, Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.SPECIFICATION, role = "specification")
@FunctionalInterface
public interface Specification<T> {

    /** Whether {@code candidate} matches. */
    boolean isSatisfiedBy(T candidate);

    /** Both this and {@code other}. */
    default Specification<T> and(Specification<T> other) {
        Objects.requireNonNull(other, "other");
        return candidate -> isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate);
    }
}
