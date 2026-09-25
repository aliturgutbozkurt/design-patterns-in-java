package io.github.aliturgutbozkurt.patterns.m00.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m00.exercises.ex01.Temperature;
import io.github.aliturgutbozkurt.patterns.m00.exercises.ex01.Unit;
import java.util.Objects;

/** Reference solution for assignment 01: a validated temperature value object. */
public record TemperatureReading(double value, Unit unit) implements Temperature {

    public TemperatureReading {
        Objects.requireNonNull(unit, "unit");
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("temperature must be a number");
        }
        if (toKelvin(value, unit) < 0) {
            throw new IllegalArgumentException("below absolute zero: " + value + " " + unit.symbol());
        }
    }

    @Override
    public Temperature to(Unit target) {
        Objects.requireNonNull(target, "target");
        if (target == unit) {
            return this; // avoids floating-point drift from a round trip through Kelvin
        }
        return new TemperatureReading(fromKelvin(toKelvin(value, unit), target), target);
    }

    private static double toKelvin(double value, Unit unit) {
        return switch (unit) {
            case CELSIUS -> value + 273.15;
            case FAHRENHEIT -> (value + 459.67) * 5 / 9;
            case KELVIN -> value;
        };
    }

    private static double fromKelvin(double kelvin, Unit unit) {
        return switch (unit) {
            case CELSIUS -> kelvin - 273.15;
            case FAHRENHEIT -> kelvin * 9 / 5 - 459.67;
            case KELVIN -> kelvin;
        };
    }
}
