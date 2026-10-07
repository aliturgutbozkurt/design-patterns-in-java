package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * A cart line with the product data pricing needs.
 *
 * @param sku       the product
 * @param name      its name
 * @param category  its category
 * @param type      physical or digital
 * @param quantity  units, ≥ 1
 * @param unitPrice list price
 * @see "capstone guide, Slice walkthrough — C4"
 */
public record BasketLine(Sku sku, String name, Category category, ProductType type, int quantity, Money unitPrice) {

    public BasketLine {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(unitPrice, "unitPrice");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
    }
}
