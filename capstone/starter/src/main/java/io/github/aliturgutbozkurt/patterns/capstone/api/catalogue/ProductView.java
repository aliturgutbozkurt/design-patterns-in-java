package io.github.aliturgutbozkurt.patterns.capstone.api.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * GIVEN — do not modify. A product as the catalogue shows it; a digital product's stock is shown as 0.
 *
 * @param sku      SKU
 * @param name     display name
 * @param category category
 * @param type     physical or digital
 * @param price    unit price (list price)
 * @param stock    units in stock, never negative
 * @see "capstone brief §2.2 — Catalogue (F1)"
 */
public record ProductView(Sku sku, String name, Category category, ProductType type, Money price, int stock) {

    public ProductView {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(price, "price");
        if (stock < 0) {
            throw new IllegalArgumentException("negative stock: " + stock);
        }
    }
}
