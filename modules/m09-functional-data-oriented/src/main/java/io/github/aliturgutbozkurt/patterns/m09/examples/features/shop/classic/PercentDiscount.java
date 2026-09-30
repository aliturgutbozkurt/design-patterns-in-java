package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * A flat percentage off the subtotal.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class PercentDiscount implements DiscountStrategy {

    private final String code;
    private final int percent;

    public PercentDiscount(String code, int percent) {
        this.code = code;
        this.percent = percent;
    }

    @Override
    public long discountCents(long subtotalCents) {
        return subtotalCents * percent / 100;
    }

    @Override
    public String name() {
        return code;
    }
}
