package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.coffee.classic;

/**
 * Component: anything the coffee shop sells, plain or decorated.
 *
 * @see "m04 lesson, section Decorator"
 */
public interface Beverage {

    String description();

    long priceInKurus();
}
