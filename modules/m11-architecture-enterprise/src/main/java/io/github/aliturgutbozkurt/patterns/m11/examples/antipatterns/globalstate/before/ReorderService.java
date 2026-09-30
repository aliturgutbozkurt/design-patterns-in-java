package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before;

import java.util.List;

/**
 * ANTI-PATTERN — see lesson: a no-argument constructor that lies — the service needs stock levels and a policy, but
 * fetches both from global state at run time.
 *
 * @see "m11 lesson, section Anti-patterns — Singleton and Service Locator abuse"
 */
public final class ReorderService {

    /** SKUs whose stock is below the policy's minimum, sorted. */
    public List<String> itemsToReorder() {
        StockLevels stock = ServiceLocator.get(StockLevels.class);   // hidden dependency 1
        int minimum = ServiceLocator.get(ReorderPolicy.class).minimumUnits(); // hidden dependency 2
        return stock.skus().stream().filter(sku -> stock.levelOf(sku) < minimum).toList();
    }
}
