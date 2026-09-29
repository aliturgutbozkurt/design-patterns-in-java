package io.github.aliturgutbozkurt.patterns.m01.examples.ocp.after;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Closed for modification: applies whatever rules it is given, in order, and never needs to know which ones exist.
 *
 * @see "m01 lesson, section OCP"
 */
public final class Checkout {

    private final List<DiscountRule> rules;

    public Checkout(List<DiscountRule> rules) {
        this.rules = List.copyOf(rules);
    }

    /** Applies every rule in order; the result is never below zero and has scale 2. */
    public BigDecimal price(BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative: " + amount);
        }
        BigDecimal price = amount;
        for (DiscountRule rule : rules) {
            price = rule.apply(price);
        }
        return price.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_EVEN);
    }
}
