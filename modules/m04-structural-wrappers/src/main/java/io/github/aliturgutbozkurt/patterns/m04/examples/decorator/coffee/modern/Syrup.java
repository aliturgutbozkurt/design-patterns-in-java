package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.modern;

import java.util.Objects;

/**
 * Decorator as a record: adds a shot of syrup to any beverage.
 *
 * @see "m04 lesson, section Decorator"
 */
public record Syrup(Beverage inner) implements Beverage {

    public Syrup {
        Objects.requireNonNull(inner, "inner");
    }

    @Override
    public String description() {
        return inner.description() + ", Syrup";
    }

    @Override
    public long priceInKurus() {
        return inner.priceInKurus() + 750;
    }
}
