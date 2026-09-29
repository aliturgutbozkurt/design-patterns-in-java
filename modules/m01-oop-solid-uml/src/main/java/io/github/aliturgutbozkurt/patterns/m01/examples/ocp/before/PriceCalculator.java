package io.github.aliturgutbozkurt.patterns.m01.examples.ocp.before;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * OCP violation: every new kind of discount means opening this class and adding another branch.
 *
 * @see "m01 lesson, section OCP"
 */
public final class PriceCalculator {

    public BigDecimal price(String customerType, BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative: " + amount);
        }
        BigDecimal percentOff;
        if (customerType.equals("REGULAR")) {
            percentOff = BigDecimal.ZERO;
        } else if (customerType.equals("STUDENT")) {
            percentOff = BigDecimal.valueOf(10);
        } else if (customerType.equals("VIP")) {
            percentOff = BigDecimal.valueOf(15);
        } else {
            throw new IllegalArgumentException("unknown customer type: " + customerType);
        }
        return amount.multiply(BigDecimal.valueOf(100).subtract(percentOff))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
    }
}
