package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Chain of Responsibility (m07) extracted from the god class: rules are asked in order, the first problem wins.
 *
 * @see "m11 lesson, section Anti-patterns — god class"
 */
public final class OrderValidator {

    private final List<Function<CheckoutRequest, Optional<String>>> rules;

    public OrderValidator(List<Function<CheckoutRequest, Optional<String>>> rules) {
        this.rules = List.copyOf(rules);
    }

    /** The shop's rules, in the order the old {@code OrderManager} checked them. */
    public static OrderValidator standard() {
        return new OrderValidator(List.of(
                request -> problemIf(request.customer() == null || request.customer().isBlank(), "missing customer"),
                request -> problemIf(request.quantity() <= 0, "invalid quantity"),
                request -> problemIf(request.unitPriceCents() <= 0, "invalid price"),
                request -> problemIf(PricingPolicy.forCustomerType(request.customerType()).isEmpty(),
                        "unknown customer type: " + request.customerType())));
    }

    /** The first rule's complaint, or empty if every rule is satisfied. */
    public Optional<String> firstProblem(CheckoutRequest request) {
        Objects.requireNonNull(request, "request");
        return rules.stream().map(rule -> rule.apply(request)).flatMap(Optional::stream).findFirst();
    }

    private static Optional<String> problemIf(boolean broken, String problem) {
        return broken ? Optional.of(problem) : Optional.empty();
    }
}
