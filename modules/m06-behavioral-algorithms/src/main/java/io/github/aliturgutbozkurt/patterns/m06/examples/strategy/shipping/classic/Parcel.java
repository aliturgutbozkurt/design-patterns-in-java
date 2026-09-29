package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * The data every shipping strategy prices: the parcel's weight and the value of the order inside it.
 *
 * @see "m06 lesson, section Strategy"
 */
public record Parcel(double weightKg, BigDecimal orderTotal) {

    public Parcel {
        if (!(weightKg >= 0)) {  // written this way so that NaN is rejected too
            throw new IllegalArgumentException("weightKg must be >= 0: " + weightKg);
        }
        Objects.requireNonNull(orderTotal, "orderTotal");
    }
}
