package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.checkout;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A link of the checkout validation chain: returns the reasons it finds (none = passed). Links combine in two ways
 * (adapted from modules/m07-…/chain/validation/Validator.java): {@link #and} runs both and collects every reason,
 * {@link #andThen} runs the next link only if this one passed (fail fast).
 *
 * @see "capstone guide §1 Pattern map — Chain of Responsibility"
 */
@PatternRole(value = DesignPattern.CHAIN_OF_RESPONSIBILITY, role = "handler")
@FunctionalInterface
public interface CheckoutRule {

    /** The reasons why {@code candidate} cannot be checked out, in order; empty when it passes. */
    List<String> check(CheckoutCandidate candidate);

    /** Collect all: both links run, their reasons are concatenated. */
    default CheckoutRule and(CheckoutRule next) {
        Objects.requireNonNull(next, "next");
        return candidate -> {
            List<String> reasons = new ArrayList<>(check(candidate));
            reasons.addAll(next.check(candidate));
            return List.copyOf(reasons);
        };
    }

    /** Fail fast: {@code next} runs only when this link found nothing. */
    default CheckoutRule andThen(CheckoutRule next) {
        Objects.requireNonNull(next, "next");
        return candidate -> {
            List<String> reasons = check(candidate);
            return reasons.isEmpty() ? next.check(candidate) : reasons;
        };
    }
}
