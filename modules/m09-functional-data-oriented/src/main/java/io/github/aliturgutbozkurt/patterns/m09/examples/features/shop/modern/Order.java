package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern;

import java.util.List;
import java.util.Objects;

/**
 * An order to invoice.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public record Order(String id, String customer, List<LineItem> items, String discountCode) {

    public Order {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(customer, "customer");
        items = List.copyOf(items);
        Objects.requireNonNull(discountCode, "discountCode");
    }
}
