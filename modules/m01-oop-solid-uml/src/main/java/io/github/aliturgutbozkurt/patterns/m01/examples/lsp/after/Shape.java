package io.github.aliturgutbozkurt.patterns.m01.examples.lsp.after;

/**
 * LSP fixed: shapes are immutable values, so no subtype can break a "setter contract" — there are no setters.
 *
 * @see "m01 lesson, section LSP"
 */
public sealed interface Shape permits Rectangle, Square {

    double area();

    double perimeter();

    /** Shared validation for all shapes. */
    static double requirePositive(String name, double value) {
        if (!(value > 0)) {
            throw new IllegalArgumentException(name + " must be positive: " + value);
        }
        return value;
    }
}
