package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern;

import java.util.Objects;

/**
 * The computed numbers of one invoice. A named record is still the clearest way to pass six related values around.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public record Invoice(Order order, long subtotalCents, String discountName, long discountCents, long taxCents,
                      int grams) {

    public Invoice {
        Objects.requireNonNull(order, "order");
        Objects.requireNonNull(discountName, "discountName");
    }

    public long totalCents() {
        return subtotalCents - discountCents + taxCents;
    }
}
