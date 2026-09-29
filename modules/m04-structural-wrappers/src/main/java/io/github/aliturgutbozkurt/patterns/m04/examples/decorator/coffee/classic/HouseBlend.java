package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic;

/**
 * Concrete component: the house filter coffee.
 *
 * @see "m04 lesson, section Decorator"
 */
public final class HouseBlend implements Beverage {

    @Override
    public String description() {
        return "House Blend";
    }

    @Override
    public long priceInKurus() {
        return 4000;
    }
}
