package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * No discount code.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class NoDiscount implements DiscountStrategy {

    @Override
    public long discountCents(long subtotalCents) {
        return 0;
    }

    @Override
    public String name() {
        return "none";
    }
}
