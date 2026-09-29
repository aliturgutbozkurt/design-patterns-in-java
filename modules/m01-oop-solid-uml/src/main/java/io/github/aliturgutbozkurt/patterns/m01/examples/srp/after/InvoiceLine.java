package io.github.aliturgutbozkurt.patterns.m01.examples.srp.after;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * One invoice line — pure data with its own validation.
 *
 * @see "m01 lesson, section SRP"
 */
public record InvoiceLine(String product, int quantity, BigDecimal unitPrice) {

    public InvoiceLine {
        Objects.requireNonNull(product, "product");
        Objects.requireNonNull(unitPrice, "unitPrice");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException("unitPrice must not be negative: " + unitPrice);
        }
        unitPrice = unitPrice.setScale(2, RoundingMode.HALF_EVEN);
    }

    /** {@code quantity × unitPrice}, scale 2. */
    public BigDecimal amount() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
