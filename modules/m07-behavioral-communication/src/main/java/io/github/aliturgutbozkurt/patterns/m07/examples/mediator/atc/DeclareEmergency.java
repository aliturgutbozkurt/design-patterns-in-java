package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.atc;

import java.util.Objects;

/**
 * "Mayday": the aircraft must land before everybody who is waiting.
 *
 * @see "m07 lesson, section Mediator — Modern Java 27"
 */
public record DeclareEmergency(Aircraft aircraft) implements TowerRequest {

    public DeclareEmergency {
        Objects.requireNonNull(aircraft, "aircraft");
    }
}
