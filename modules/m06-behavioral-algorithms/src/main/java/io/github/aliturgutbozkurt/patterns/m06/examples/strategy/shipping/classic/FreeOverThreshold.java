package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Concrete strategy: free shipping when the order total reaches the threshold (inclusive), otherwise it delegates to
 * another strategy.
 *
 * @see "m06 lesson, section Strategy"
 */
public final class FreeOverThreshold implements ShippingStrategy {

    private static final BigDecimal FREE = new BigDecimal("0.00");

    private final BigDecimal threshold;
    private final ShippingStrategy otherwise;

    public FreeOverThreshold(BigDecimal threshold, ShippingStrategy otherwise) {
        this.threshold = Objects.requireNonNull(threshold, "threshold");
        this.otherwise = Objects.requireNonNull(otherwise, "otherwise");
    }

    @Override
    public BigDecimal cost(Parcel parcel) {
        return parcel.orderTotal().compareTo(threshold) >= 0 ? FREE : otherwise.cost(parcel);
    }

    @Override
    public String name() {
        return "free-over-" + threshold;
    }
}
