package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * One line of an order, with the product data as it was at checkout.
 *
 * @param sku       the product
 * @param name      its name
 * @param type      physical or digital
 * @param quantity  units, ≥ 1
 * @param unitPrice list price
 * @see "capstone guide, Slice walkthrough — C3"
 */
public record OrderItem(Sku sku, String name, ProductType type, int quantity, Money unitPrice) {

    public OrderItem {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(unitPrice, "unitPrice");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
    }

    /** Whether the warehouse has to pick this line. */
    public boolean isPhysical() {
        return type == ProductType.PHYSICAL;
    }
}
