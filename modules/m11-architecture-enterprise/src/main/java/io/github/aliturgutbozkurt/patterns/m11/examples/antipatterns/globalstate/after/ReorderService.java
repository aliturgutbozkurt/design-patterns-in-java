package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.after;

import java.util.List;
import java.util.Objects;
import java.util.function.IntSupplier;

/**
 * The same decision with constructor injection: the parameter list <em>is</em> the dependency list.
 *
 * @see "m11 lesson, section Anti-patterns — Singleton and Service Locator abuse"
 */
public final class ReorderService {

    private final StockLevels stock;
    private final IntSupplier minimumUnits;

    public ReorderService(StockLevels stock, IntSupplier minimumUnits) {
        this.stock = Objects.requireNonNull(stock, "stock");
        this.minimumUnits = Objects.requireNonNull(minimumUnits, "minimumUnits");
    }

    /** SKUs whose stock is below the minimum, sorted. */
    public List<String> itemsToReorder() {
        int minimum = minimumUnits.getAsInt();
        return stock.skus().stream().filter(sku -> stock.levelOf(sku) < minimum).toList();
    }
}
