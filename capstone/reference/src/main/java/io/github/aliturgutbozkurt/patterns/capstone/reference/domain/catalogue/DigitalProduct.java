package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * A product delivered electronically: unlimited stock (shown as 0), no address, no warehouse.
 *
 * @param sku      SKU
 * @param name     non-blank name
 * @param category category
 * @param price    positive unit price
 * @see "capstone guide §2 Slice walkthrough — C3"
 */
public record DigitalProduct(Sku sku, String name, Category category, Money price) implements Product {

    public DigitalProduct {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(category, "category");
        ProductRules.requireNameAndPrice(name, price);
    }

    @Override
    public ProductType type() {
        return ProductType.DIGITAL;
    }

    @Override
    public int stock() {
        return 0;
    }
}
