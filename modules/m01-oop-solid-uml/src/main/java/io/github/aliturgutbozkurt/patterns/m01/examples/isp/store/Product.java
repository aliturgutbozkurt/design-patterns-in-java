package io.github.aliturgutbozkurt.patterns.m01.examples.isp.store;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * A catalog product; shared by the before and after versions.
 *
 * @see "m01 lesson, section ISP"
 */
public record Product(String sku, String name, BigDecimal price) {

    public Product {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(name, "name");
        if (price.signum() < 0) {
            throw new IllegalArgumentException("price must not be negative: " + price);
        }
        price = price.setScale(2, RoundingMode.HALF_EVEN);
    }
}
