package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;

/**
 * Creates the right {@link Product} for a {@link ProductType}: one constant per type, each carrying its creation step
 * (a constructor reference), so callers never branch on the type.
 *
 * @see "capstone guide §1 Pattern map — Factory Method"
 */
@PatternRole(value = DesignPattern.FACTORY_METHOD, role = "creator: one constant per product type with its factory method")
public enum ProductFactory {
    PHYSICAL(PhysicalProduct::new),
    DIGITAL(ProductFactory::digital);

    /** The creation step of one constant. */
    @FunctionalInterface
    private interface Creation {
        Product create(Sku sku, String name, Category category, Money price, int initialStock);
    }

    private final Creation creation;

    ProductFactory(Creation creation) {
        this.creation = creation;
    }

    /** The factory for {@code type}. */
    public static ProductFactory forType(ProductType type) {
        return switch (type) {
            case PHYSICAL -> PHYSICAL;
            case DIGITAL -> DIGITAL;
        };
    }

    /** A new product; invalid data is rejected with {@link IllegalArgumentException}. */
    public Product create(Sku sku, String name, Category category, Money price, int initialStock) {
        return creation.create(sku, name, category, price, initialStock);
    }

    private static Product digital(Sku sku, String name, Category category, Money price, int initialStock) {
        if (initialStock != 0) {
            throw new IllegalArgumentException("a digital product has no stock: " + initialStock);
        }
        return new DigitalProduct(sku, name, category, price);
    }
}
