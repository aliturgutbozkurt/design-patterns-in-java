package io.github.aliturgutbozkurt.patterns.m00.exercises.ex01;

/** Assignment 01 — your record. See assignments/01-temperature.en.md (Türkçe: 01-temperature.tr.md). */
public record TemperatureReading(double value, Unit unit) implements Temperature {

    public TemperatureReading {
        // TODO(ex01): reject a null unit (NullPointerException), NaN, and values below absolute zero
        //             (0 K = -273.15 °C = -459.67 °F) with an IllegalArgumentException.
    }

    @Override
    public Temperature to(Unit target) {
        // TODO(ex01): return the same temperature in the target unit (hint: convert through Kelvin).
        throw new UnsupportedOperationException("TODO(ex01): implement to(Unit)");
    }
}
