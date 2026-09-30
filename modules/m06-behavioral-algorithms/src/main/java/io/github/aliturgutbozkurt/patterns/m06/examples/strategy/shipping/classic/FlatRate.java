package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Concrete strategy: the same fee for every parcel.
 *
 * @see "m06 lesson, section Strategy"
 */
public final class FlatRate implements ShippingStrategy {

    private final BigDecimal fee;

    public FlatRate(BigDecimal fee) {
        this.fee = Objects.requireNonNull(fee, "fee");
    }

    @Override
    public BigDecimal cost(Parcel parcel) {
        return fee;
    }

    @Override
    public String name() {
        return "flat";
    }
}
