package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.modern;

import java.util.Objects;

/**
 * Decorator as a record: adds an extra espresso shot to any beverage.
 *
 * @see "m04 lesson, section Decorator"
 */
public record ExtraShot(Beverage inner) implements Beverage {

    public ExtraShot {
        Objects.requireNonNull(inner, "inner");
    }

    @Override
    public String description() {
        return inner.description() + ", Extra Shot";
    }

    @Override
    public long priceInKurus() {
        return inner.priceInKurus() + 1500;
    }
}
