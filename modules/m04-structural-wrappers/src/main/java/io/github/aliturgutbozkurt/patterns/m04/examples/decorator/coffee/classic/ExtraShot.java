package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic;

/**
 * Concrete decorator: adds an extra espresso shot to any beverage.
 *
 * @see "m04 lesson, section Decorator"
 */
public final class ExtraShot extends CondimentDecorator {

    public ExtraShot(Beverage beverage) {
        super(beverage);
    }

    @Override
    public String description() {
        return beverage.description() + ", Extra Shot";
    }

    @Override
    public long priceInKurus() {
        return beverage.priceInKurus() + 1500;
    }
}
