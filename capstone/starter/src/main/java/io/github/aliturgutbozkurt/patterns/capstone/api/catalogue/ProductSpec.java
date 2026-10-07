package io.github.aliturgutbozkurt.patterns.capstone.api.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * GIVEN — do not modify. What {@link CatalogueUseCase#add} receives. Only {@code null} is rejected here; the business
 * rules (non-blank name, positive price, valid stock) are the catalogue's job.
 *
 * @param sku          the new product's SKU
 * @param name         display name
 * @param category     category
 * @param type         physical or digital
 * @param price        unit price
 * @param initialStock units in stock (physical, ≥ 0) or 0 (digital)
 * @see "capstone brief §2.2 — Catalogue (F1)"
 */
public record ProductSpec(Sku sku, String name, Category category, ProductType type, Money price, int initialStock) {

    public ProductSpec {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(price, "price");
    }
}
