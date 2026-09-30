package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A catalogue product; the record rejects values that can never be right (blank SKU, negative price).
 *
 * @see "m06 lesson, section Template Method"
 */
public record Product(String sku, String name, BigDecimal price) {

    public Product {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(price, "price");
        if (sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (price.signum() < 0) {
            throw new IllegalArgumentException("price must be >= 0: " + price);
        }
    }
}
