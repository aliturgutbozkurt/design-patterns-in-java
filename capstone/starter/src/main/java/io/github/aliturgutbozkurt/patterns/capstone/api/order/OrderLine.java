package io.github.aliturgutbozkurt.patterns.capstone.api.order;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * GIVEN — do not modify. One line of an order, with the product data as it was at checkout.
 *
 * @param sku       the product
 * @param name      its name
 * @param type      physical or digital
 * @param quantity  units, at least 1
 * @param unitPrice list price at checkout
 * @see "capstone brief, Business rules — Order lifecycle (F7)"
 */
public record OrderLine(Sku sku, String name, ProductType type, int quantity, Money unitPrice) {

    public OrderLine {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(unitPrice, "unitPrice");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
    }
}
