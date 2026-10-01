package io.github.aliturgutbozkurt.patterns.m09.examples.immutability.cart;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A deeply immutable cart: the compact constructor copies the lines ({@link List#copyOf}), and every "change" is a
 * wither that returns a new {@code Cart}.
 *
 * @see "m09 lesson, section Immutability and value objects"
 */
public record Cart(List<CartLine> lines) {

    public Cart {
        lines = List.copyOf(lines);
    }

    public Cart withLine(CartLine line) {
        Objects.requireNonNull(line, "line");
        return new Cart(Stream.concat(lines.stream(), Stream.of(line)).toList());
    }

    public Cart withoutSku(String sku) {
        return new Cart(lines.stream().filter(line -> !line.sku().equals(sku)).toList());
    }

    /** Sets the quantity of {@code sku}; {@code 0} removes the line. */
    public Cart withQuantity(String sku, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must not be negative: " + quantity);
        }
        if (quantity == 0) {
            return withoutSku(sku);
        }
        return new Cart(lines.stream()
                .map(line -> line.sku().equals(sku) ? line.withQuantity(quantity) : line)
                .toList());
    }

    public long totalCents() {
        return lines.stream().mapToLong(CartLine::totalCents).sum();
    }
}
