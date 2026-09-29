package io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression;

import java.util.Objects;

/**
 * Composite: {@code left + right}.
 *
 * @see "m05 lesson, section Composite — modern Java 27"
 */
public record Add(Expr left, Expr right) implements Expr {

    public Add {
        Objects.requireNonNull(left, "left");
        Objects.requireNonNull(right, "right");
    }
}
