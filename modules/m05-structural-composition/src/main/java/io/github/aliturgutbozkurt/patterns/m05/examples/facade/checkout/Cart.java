package io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * What the customer wants to buy: at least one line of (sku, quantity, unit price in cents).
 *
 * @see "m05 lesson, section Facade — modern Java 27"
 */
public record Cart(List<Line> lines) {

    /**
     * One cart line.
     *
     * @see "m05 lesson, section Facade — modern Java 27"
     */
    public record Line(String sku, int quantity, long unitPriceCents) {
        public Line {
            Objects.requireNonNull(sku, "sku");
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be positive: " + quantity);
            }
            if (unitPriceCents < 0) {
                throw new IllegalArgumentException("price must not be negative: " + unitPriceCents);
            }
        }
    }

    public Cart {
        lines = List.copyOf(lines);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("a cart needs at least one line");
        }
    }

    public static Cart of(Line... lines) {
        return new Cart(List.of(lines));
    }

    public long totalCents() {
        return lines.stream().mapToLong(line -> line.quantity() * line.unitPriceCents()).sum();
    }

    /** Quantity per sku, lines for the same sku added together. */
    public Map<String, Integer> quantities() {
        var quantities = new LinkedHashMap<String, Integer>();
        lines.forEach(line -> quantities.merge(line.sku(), line.quantity(), Integer::sum));
        return Map.copyOf(quantities);
    }
}
