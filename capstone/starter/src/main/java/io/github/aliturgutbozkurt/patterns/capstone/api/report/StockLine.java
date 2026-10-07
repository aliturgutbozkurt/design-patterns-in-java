package io.github.aliturgutbozkurt.patterns.capstone.api.report;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * GIVEN — do not modify. One physical product in an inventory report.
 *
 * @param sku   the product
 * @param name  its name
 * @param stock units in stock
 * @param low   whether the stock is below the low-stock threshold
 * @see "capstone brief §2.2 — Reports (F10)"
 */
public record StockLine(Sku sku, String name, int stock, boolean low) {

    public StockLine {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
    }
}
