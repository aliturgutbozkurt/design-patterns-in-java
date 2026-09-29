package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.atc;

import java.util.Objects;

/**
 * "Request permission to take off."
 *
 * @see "m07 lesson, section Mediator — Modern Java 27"
 */
public record RequestTakeoff(Aircraft aircraft) implements TowerRequest {

    public RequestTakeoff {
        Objects.requireNonNull(aircraft, "aircraft");
    }
}
