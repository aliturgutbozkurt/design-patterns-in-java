package io.github.aliturgutbozkurt.patterns.capstone.api.report;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * GIVEN — do not modify. One row of a top-products report.
 *
 * @param rank    1, 2, …
 * @param sku     the product
 * @param name    its name
 * @param units   units sold
 * @param revenue units × list price
 * @see "capstone brief §2.2 — Reports (F10)"
 */
public record ProductSales(int rank, Sku sku, String name, int units, Money revenue) {

    public ProductSales {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(revenue, "revenue");
    }
}
