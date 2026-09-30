package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.modern;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Static factories that return shipping strategies as lambdas, plus a higher-order function that combines them.
 *
 * @see "m06 lesson, section Strategy"
 */
public final class ShippingRules {

    private static final BigDecimal FREE = new BigDecimal("0.00");

    private ShippingRules() {}

    public static ShippingRule flatRate(BigDecimal fee) {
        Objects.requireNonNull(fee, "fee");
        return _ -> fee;
    }

    public static ShippingRule weightBased(BigDecimal baseFee, BigDecimal perKg) {
        Objects.requireNonNull(baseFee, "baseFee");
        Objects.requireNonNull(perKg, "perKg");
        return parcel -> baseFee.add(perKg.multiply(BigDecimal.valueOf(parcel.weightKg())))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static ShippingRule freeOver(BigDecimal threshold, ShippingRule otherwise) {
        Objects.requireNonNull(threshold, "threshold");
        Objects.requireNonNull(otherwise, "otherwise");
        return parcel -> parcel.orderTotal().compareTo(threshold) >= 0 ? FREE : otherwise.cost(parcel);
    }

    /** A rule that asks every rule and keeps the lowest price: strategies composed into a new strategy. */
    public static ShippingRule cheapestOf(ShippingRule... rules) {
        if (rules.length == 0) {
            throw new IllegalArgumentException("cheapestOf needs at least one rule");
        }
        List<ShippingRule> copy = List.copyOf(Arrays.asList(rules));
        return parcel -> copy.stream()
                .map(rule -> rule.cost(parcel))
                .min(BigDecimal::compareTo)
                .orElseThrow();
    }
}
