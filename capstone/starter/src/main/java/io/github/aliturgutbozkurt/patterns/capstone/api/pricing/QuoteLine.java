package io.github.aliturgutbozkurt.patterns.capstone.api.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * GIVEN — do not modify. One priced cart line before discounts.
 *
 * @param sku       the product
 * @param name      its name
 * @param quantity  units
 * @param unitPrice list price
 * @param lineTotal {@code quantity × unitPrice}
 * @see "capstone brief, Business rules — Pricing (F4), step 1"
 */
public record QuoteLine(Sku sku, String name, int quantity, Money unitPrice, Money lineTotal) {

    public QuoteLine {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(unitPrice, "unitPrice");
        Objects.requireNonNull(lineTotal, "lineTotal");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
    }
}
