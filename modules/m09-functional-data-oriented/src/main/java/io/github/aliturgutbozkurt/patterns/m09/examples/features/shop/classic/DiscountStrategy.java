package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * Strategy: how much to take off a subtotal.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public interface DiscountStrategy {

    long discountCents(long subtotalCents);

    String name();
}
