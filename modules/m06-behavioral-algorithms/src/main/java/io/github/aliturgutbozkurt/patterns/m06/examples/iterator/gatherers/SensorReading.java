package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.gatherers;

import java.util.Objects;

/**
 * One measurement from an IoT sensor, taken at {@code second} seconds after the start.
 *
 * @see "m06 lesson, section Iterator"
 */
public record SensorReading(String sensor, int second, double value) {

    public SensorReading {
        Objects.requireNonNull(sensor, "sensor");
    }
}
