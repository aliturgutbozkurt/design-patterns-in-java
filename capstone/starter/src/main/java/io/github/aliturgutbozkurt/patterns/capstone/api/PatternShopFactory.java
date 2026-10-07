package io.github.aliturgutbozkurt.patterns.capstone.api;

/**
 * GIVEN — do not modify. Builds a shop from its environment; your composition root implements it, and it is the only
 * way the acceptance tests create your shop.
 *
 * @see "capstone brief §6 — What you are given"
 */
@FunctionalInterface
public interface PatternShopFactory {

    /** A new, empty shop (no products, no promotions) wired to {@code env}. */
    PatternShop create(ShopEnvironment env);
}
