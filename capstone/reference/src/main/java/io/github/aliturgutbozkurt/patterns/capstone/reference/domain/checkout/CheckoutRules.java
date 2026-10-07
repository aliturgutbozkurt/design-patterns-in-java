package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.checkout;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.List;

/**
 * The checkout rules of the brief's Business rules as chain links, and the standard chain: {@code empty cart} alone
 * (fail fast), otherwise every rule in this order, collecting all reasons.
 *
 * @see "capstone guide, Pattern map — Chain of Responsibility"
 */
@PatternRole(value = DesignPattern.CHAIN_OF_RESPONSIBILITY, role = "concrete handlers and chain assembly")
public final class CheckoutRules {

    /** At most this many units of one SKU per order. */
    public static final int MAX_UNITS_PER_SKU = 10;

    private CheckoutRules() {
    }

    /** The standard chain. */
    public static CheckoutRule standard() {
        return nonEmptyCart().andThen(addressForPhysicalItems()
                .and(quantityLimit())
                .and(stockAvailable())
                .and(couponNotExpired())
                .and(cardTokenPresent()));
    }

    public static CheckoutRule nonEmptyCart() {
        return candidate -> candidate.lines().isEmpty() ? List.of("empty cart") : List.of();
    }

    public static CheckoutRule addressForPhysicalItems() {
        return candidate -> candidate.lines().stream().anyMatch(CandidateLine::isPhysical)
                && !candidate.address().isComplete() ? List.of("missing address") : List.of();
    }

    public static CheckoutRule quantityLimit() {
        return candidate -> candidate.lines().stream()
                .filter(line -> line.quantity() > MAX_UNITS_PER_SKU)
                .map(line -> "quantity limit exceeded: " + line.sku().value())
                .toList();
    }

    public static CheckoutRule stockAvailable() {
        return candidate -> candidate.lines().stream()
                .filter(line -> line.isPhysical() && line.quantity() > line.stock())
                .map(line -> "insufficient stock: " + line.sku().value())
                .toList();
    }

    public static CheckoutRule couponNotExpired() {
        return candidate -> candidate.couponExpired() ? List.of("expired coupon: " + candidate.coupon()) : List.of();
    }

    public static CheckoutRule cardTokenPresent() {
        return candidate -> candidate.cardToken().isBlank() ? List.of("missing card token") : List.of();
    }
}
