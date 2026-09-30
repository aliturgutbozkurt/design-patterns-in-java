package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic;

/**
 * Concrete decorator: adds a shot of syrup to any beverage.
 *
 * @see "m04 lesson, section Decorator"
 */
public final class Syrup extends CondimentDecorator {

    public Syrup(Beverage beverage) {
        super(beverage);
    }

    @Override
    public String description() {
        return beverage.description() + ", Syrup";
    }

    @Override
    public long priceInKurus() {
        return beverage.priceInKurus() + 750;
    }
}
