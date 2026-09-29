package io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression;

import java.util.Objects;

/**
 * Composite with one child: {@code -operand}.
 *
 * @see "m05 lesson, section Composite — modern Java 27"
 */
public record Neg(Expr operand) implements Expr {

    public Neg {
        Objects.requireNonNull(operand, "operand");
    }
}
