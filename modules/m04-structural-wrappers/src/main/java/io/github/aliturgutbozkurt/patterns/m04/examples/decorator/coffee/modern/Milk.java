package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.modern;

import java.util.Objects;

/**
 * Decorator as a record: the wrapped component is a record component, no abstract base needed.
 *
 * @see "m04 lesson, section Decorator"
 */
public record Milk(Beverage inner) implements Beverage {

    public Milk {
        Objects.requireNonNull(inner, "inner");
    }

    @Override
    public String description() {
        return inner.description() + ", Milk";
    }

    @Override
    public long priceInKurus() {
        return inner.priceInKurus() + 500;
    }
}
