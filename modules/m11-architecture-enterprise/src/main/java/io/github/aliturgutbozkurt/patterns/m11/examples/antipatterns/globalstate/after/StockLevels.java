package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.after;

import java.util.List;

/**
 * Stock levels as an interface: each caller gets the instance it is given — one per test, one per tenant.
 *
 * @see "m11 lesson, section Anti-patterns — Singleton and Service Locator abuse"
 */
public interface StockLevels {

    int levelOf(String sku);

    /** Every known SKU, sorted. */
    List<String> skus();
}
