package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.atc;

import java.util.Objects;

/**
 * "Runway vacated": the aircraft has left the runway, the tower may clear the next one.
 *
 * @see "m07 lesson, section Mediator — Modern Java 27"
 */
public record RunwayVacated(Aircraft aircraft) implements TowerRequest {

    public RunwayVacated {
        Objects.requireNonNull(aircraft, "aircraft");
    }
}
