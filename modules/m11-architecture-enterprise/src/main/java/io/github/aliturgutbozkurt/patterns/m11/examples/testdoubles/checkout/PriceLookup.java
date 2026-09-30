package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.checkout;

/**
 * Where prices come from (a query: replace it with a stub).
 *
 * @see "m11 lesson, section Test doubles"
 */
@FunctionalInterface
public interface PriceLookup {

    long priceCents(String sku);
}
