package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic;

import java.util.Objects;

/**
 * Decorator base: is a {@link Beverage} and has a {@link Beverage}, so condiments can wrap each other.
 *
 * @see "m04 lesson, section Decorator"
 */
public abstract class CondimentDecorator implements Beverage {

    protected final Beverage beverage;                       // the wrapped component

    protected CondimentDecorator(Beverage beverage) {
        this.beverage = Objects.requireNonNull(beverage, "beverage");
    }
}
