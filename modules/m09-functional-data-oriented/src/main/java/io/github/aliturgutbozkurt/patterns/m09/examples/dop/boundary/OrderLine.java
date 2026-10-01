package io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary;

import java.util.Objects;

/**
 * A parsed order line: built only from valid parts, so the core never re-checks it.
 *
 * @see "m09 lesson, section Data-oriented programming — parse, don't validate"
 */
public record OrderLine(Sku sku, Quantity quantity, long unitPriceCents) {

    public OrderLine {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(quantity, "quantity");
        if (unitPriceCents < 0) {
            throw new IllegalArgumentException("unitPriceCents must not be negative: " + unitPriceCents);
        }
    }
}
