package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * 5 % off, but only from a subtotal of 100.00.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class BulkDiscount implements DiscountStrategy {

    @Override
    public long discountCents(long subtotalCents) {
        return subtotalCents >= 100_00 ? subtotalCents * 5 / 100 : 0;
    }

    @Override
    public String name() {
        return "BULK";
    }
}
