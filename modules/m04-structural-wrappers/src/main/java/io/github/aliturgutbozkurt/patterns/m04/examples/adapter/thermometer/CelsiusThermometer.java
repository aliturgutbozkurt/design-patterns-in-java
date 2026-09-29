package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.thermometer;

/**
 * Target: the interface our smart-home code expects. It has one method, so a lambda can implement it.
 *
 * @see "m04 lesson, section Adapter"
 */
@FunctionalInterface
public interface CelsiusThermometer {

    /** The current temperature in degrees Celsius. */
    double readCelsius();
}
