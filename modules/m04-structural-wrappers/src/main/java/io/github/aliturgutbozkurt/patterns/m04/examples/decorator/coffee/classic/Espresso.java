package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic;

/**
 * Concrete component: a plain espresso.
 *
 * @see "m04 lesson, section Decorator"
 */
public final class Espresso implements Beverage {

    @Override
    public String description() {
        return "Espresso";
    }

    @Override
    public long priceInKurus() {
        return 4500;
    }
}
