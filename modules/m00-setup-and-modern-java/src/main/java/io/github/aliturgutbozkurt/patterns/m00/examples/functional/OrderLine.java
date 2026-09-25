package io.github.aliturgutbozkurt.patterns.m00.examples.functional;

import io.github.aliturgutbozkurt.patterns.m00.examples.records.Money;
import java.util.Objects;

/** One line of a customer order. */
public record OrderLine(String product, String category, int quantity, Money unitPrice) {

    public OrderLine {
        Objects.requireNonNull(product, "product");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(unitPrice, "unitPrice");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
    }

    public Money total() {
        return unitPrice.times(quantity);
    }
}
