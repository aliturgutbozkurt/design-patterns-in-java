package io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary;

/**
 * How many items of one SKU a line orders: 1 to 99.
 *
 * @see "m09 lesson, section Data-oriented programming — parse, don't validate"
 */
public record Quantity(int value) {

    public static final int MAX = 99;

    public Quantity {
        if (value < 1 || value > MAX) {
            throw new IllegalArgumentException("quantity must be 1.." + MAX + ": " + value);
        }
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
