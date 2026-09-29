package io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Something on the menu: a name and a price, fixed when the item is constructed.
 *
 * @see "m03 lesson, section Builder"
 */
public abstract class MenuItem {

    private final String name;
    private final BigDecimal price;

    protected MenuItem(String name, BigDecimal price) {
        this.name = Objects.requireNonNull(name, "name");
        if (price.signum() < 0) {
            throw new IllegalArgumentException("price must not be negative: " + price);
        }
        this.price = price;
    }

    public String name() {
        return name;
    }

    public BigDecimal price() {
        return price;
    }
}
