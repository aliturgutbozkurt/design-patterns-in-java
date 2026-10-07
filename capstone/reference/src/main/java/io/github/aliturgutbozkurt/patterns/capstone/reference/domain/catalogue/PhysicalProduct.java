package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * A product that is shipped from the warehouse and has a stock.
 *
 * @param sku      SKU
 * @param name     non-blank name
 * @param category category
 * @param price    positive unit price
 * @param stock    units in stock, ≥ 0
 * @see "capstone guide §2 Slice walkthrough — C3"
 */
public record PhysicalProduct(Sku sku, String name, Category category, Money price, int stock) implements Product {

    public PhysicalProduct {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(category, "category");
        ProductRules.requireNameAndPrice(name, price);
        if (stock < 0) {
            throw new IllegalArgumentException("negative stock: " + stock);
        }
    }

    @Override
    public ProductType type() {
        return ProductType.PHYSICAL;
    }

    /** This product with {@code quantity} (≥ 1) more units. */
    public PhysicalProduct restocked(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("restock quantity must be positive: " + quantity);
        }
        return new PhysicalProduct(sku, name, category, price, Math.addExact(stock, quantity));
    }

    /** This product with {@code quantity} units taken out of the stock (the caller has checked there are enough). */
    public PhysicalProduct reserved(int quantity) {
        if (quantity < 1 || quantity > stock) {
            throw new IllegalStateException("cannot reserve " + quantity + " of " + stock + " " + sku.value());
        }
        return new PhysicalProduct(sku, name, category, price, stock - quantity);
    }
}
