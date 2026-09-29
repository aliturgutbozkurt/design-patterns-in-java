package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic;

/**
 * Concrete decorator: adds milk to any beverage.
 *
 * @see "m04 lesson, section Decorator"
 */
public final class Milk extends CondimentDecorator {

    public Milk(Beverage beverage) {
        super(beverage);
    }

    @Override
    public String description() {
        return beverage.description() + ", Milk";            // delegate, then add
    }

    @Override
    public long priceInKurus() {
        return beverage.priceInKurus() + 500;
    }
}
