package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Concrete strategy: a base fee plus a rate per kilogram.
 *
 * @see "m06 lesson, section Strategy"
 */
public final class WeightBased implements ShippingStrategy {

    private final BigDecimal baseFee;
    private final BigDecimal perKg;

    public WeightBased(BigDecimal baseFee, BigDecimal perKg) {
        this.baseFee = Objects.requireNonNull(baseFee, "baseFee");
        this.perKg = Objects.requireNonNull(perKg, "perKg");
    }

    @Override
    public BigDecimal cost(Parcel parcel) {
        BigDecimal weight = BigDecimal.valueOf(parcel.weightKg());
        return baseFee.add(perKg.multiply(weight)).setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public String name() {
        return "per-kg";
    }
}
