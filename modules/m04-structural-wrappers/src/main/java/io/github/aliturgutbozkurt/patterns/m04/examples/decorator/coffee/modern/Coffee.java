package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.modern;

/**
 * Concrete components: the base coffees, as enum constants.
 *
 * @see "m04 lesson, section Decorator"
 */
public enum Coffee implements Beverage {
    ESPRESSO("Espresso", 4500),
    HOUSE_BLEND("House Blend", 4000);

    private final String description;
    private final long priceInKurus;

    Coffee(String description, long priceInKurus) {
        this.description = description;
        this.priceInKurus = priceInKurus;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public long priceInKurus() {
        return priceInKurus;
    }
}
