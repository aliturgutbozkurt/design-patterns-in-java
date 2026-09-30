package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after;

import java.util.Optional;

/**
 * Strategy (m06) extracted from the god class's {@code if/else} on the customer type.
 *
 * @see "m11 lesson, section Anti-patterns — god class"
 */
@FunctionalInterface
public interface PricingPolicy {

    long totalCents(int quantity, long unitPriceCents);

    /** The policy for a customer type, or empty for an unknown type. */
    static Optional<PricingPolicy> forCustomerType(String customerType) {
        return switch (customerType) {
            case "REGULAR" -> Optional.of(percentOff(0));
            case "VIP" -> Optional.of(percentOff(10));
            case "EMPLOYEE" -> Optional.of(percentOff(30));
            case null, default -> Optional.empty();
        };
    }

    private static PricingPolicy percentOff(int percent) {
        return (quantity, unitPriceCents) -> {
            long total = quantity * unitPriceCents * (100 - percent) / 100;
            return quantity >= 10 ? total * 95 / 100 : total; // bulk discount on top
        };
    }
}
