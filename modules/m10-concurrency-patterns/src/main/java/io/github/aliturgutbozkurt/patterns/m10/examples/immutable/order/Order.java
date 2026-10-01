package io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * Immutable Object: a shop order that any number of threads can read without a lock. All fields are final, the
 * line list is copied into an unmodifiable list ({@code List.copyOf}), and every "change" is a wither that returns
 * a new order. Nothing can change after construction, so there is nothing to synchronise.
 *
 * @see "m10 lesson, section Immutable Object"
 */
public record Order(String id, List<OrderLine> lines) {

    public Order {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        lines = List.copyOf(lines);                 // defensive copy + unmodifiable, in one call
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("order " + id + " needs at least one line");
        }
        Currency currency = lines.getFirst().unitPrice().currency();
        for (OrderLine line : lines) {
            if (!line.unitPrice().currency().equals(currency)) {
                throw new IllegalArgumentException("mixed currencies in order " + id + ": " + currency + " and "
                        + line.unitPrice().currency());
            }
        }
    }

    /** A new order with {@code line} appended; this order is unchanged. */
    public Order withLine(OrderLine line) {
        List<OrderLine> next = new ArrayList<>(lines);
        next.add(line);
        return new Order(id, next);
    }

    /** A new order without the lines for {@code sku}; this order is unchanged. */
    public Order withoutSku(String sku) {
        return new Order(id, lines.stream().filter(line -> !line.sku().equals(sku)).toList());
    }

    public Money total() {
        return lines.stream().map(OrderLine::total).reduce(Money::plus).orElseThrow();
    }
}
