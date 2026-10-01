package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * Factory: picks the strategy for a discount code.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public final class DiscountStrategies {

    private DiscountStrategies() {}

    public static DiscountStrategy forCode(String code) {
        if (code.isBlank()) {
            return new NoDiscount();
        } else if (code.equals("TEN")) {
            return new PercentDiscount("TEN", 10);
        } else if (code.equals("BULK")) {
            return new BulkDiscount();
        }
        throw new IllegalArgumentException("unknown discount code: " + code);
    }
}
