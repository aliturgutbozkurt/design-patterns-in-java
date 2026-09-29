package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.thermometer;

import java.util.Objects;

/**
 * Object adapter: makes a {@link FahrenheitSensor} usable wherever a {@link CelsiusThermometer} is expected.
 *
 * @see "m04 lesson, section Adapter"
 */
public final class FahrenheitAdapter implements CelsiusThermometer {

    private final FahrenheitSensor sensor;                   // the adaptee, held by composition

    public FahrenheitAdapter(FahrenheitSensor sensor) {
        this.sensor = Objects.requireNonNull(sensor, "sensor");
    }

    @Override
    public double readCelsius() {
        return toCelsius(sensor.readFahrenheit());           // translate the call and the unit
    }

    public static double toCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }
}
