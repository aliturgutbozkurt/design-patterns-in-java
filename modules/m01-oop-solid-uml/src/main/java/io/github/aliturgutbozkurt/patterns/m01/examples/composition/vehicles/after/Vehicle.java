package io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.after;

import java.util.Locale;
import java.util.Objects;

/**
 * Composition: a vehicle <em>has</em> an engine and a gearbox, so the two dimensions vary independently.
 *
 * @see "m01 lesson, section Composition over inheritance"
 */
public record Vehicle(Engine engine, Gearbox gearbox) {

    public Vehicle {
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(gearbox, "gearbox");
    }

    public int rangeKm() {
        double range = switch (engine) {
            case Engine.Petrol(double tank, double per100) -> tank * 100 / per100;
            case Engine.Diesel(double tank, double per100) -> tank * 100 / per100;
            case Engine.Electric(double battery, double per100) -> battery * 100 / per100;
        };
        return (int) Math.round(range);
    }

    public String describe() {
        String engineName = switch (engine) {
            case Engine.Petrol _ -> "Petrol";
            case Engine.Diesel _ -> "Diesel";
            case Engine.Electric _ -> "Electric";
        };
        return engineName + " engine, " + gearbox.name().toLowerCase(Locale.ROOT) + " gearbox, range " + rangeKm() + " km";
    }
}
