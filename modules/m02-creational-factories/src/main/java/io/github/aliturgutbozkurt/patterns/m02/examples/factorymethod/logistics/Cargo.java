package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics;

import java.util.Objects;

/**
 * What to deliver and how far.
 *
 * @see "m02 lesson, section Factory Method"
 */
public record Cargo(String description, int kilograms, int distanceKm) {

    public Cargo {
        Objects.requireNonNull(description, "description");
        if (kilograms <= 0) {
            throw new IllegalArgumentException("kilograms must be positive: " + kilograms);
        }
        if (distanceKm <= 0) {
            throw new IllegalArgumentException("distanceKm must be positive: " + distanceKm);
        }
    }
}
