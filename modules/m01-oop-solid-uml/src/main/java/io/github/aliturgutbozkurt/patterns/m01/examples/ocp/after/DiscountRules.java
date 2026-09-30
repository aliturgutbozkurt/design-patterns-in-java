package io.github.aliturgutbozkurt.patterns.m01.examples.ocp.after;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Ready-made rules as static factories. Adding a rule here does not touch {@link Checkout} either.
 *
 * @see "m01 lesson, section OCP"
 */
public final class DiscountRules {

    private DiscountRules() {}

    /** {@code percent} % off, rounded half-even to two decimals. */
    public static DiscountRule percentOff(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be in 0..100: " + percent);
        }
        BigDecimal factor = BigDecimal.valueOf(100L - percent);
        return price -> price.multiply(factor).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
    }

    /** A fixed amount off. */
    public static DiscountRule fixedOff(BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative: " + amount);
        }
        return price -> price.subtract(amount);
    }

    /** Applies {@code rule} only when the price has reached {@code threshold}. */
    public static DiscountRule minimumSpend(BigDecimal threshold, DiscountRule rule) {
        Objects.requireNonNull(threshold, "threshold");
        Objects.requireNonNull(rule, "rule");
        return price -> price.compareTo(threshold) >= 0 ? rule.apply(price) : price;
    }
}
