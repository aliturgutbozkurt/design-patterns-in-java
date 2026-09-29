package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.atc;

import java.util.Objects;

/**
 * "Request permission to land."
 *
 * @see "m07 lesson, section Mediator — Modern Java 27"
 */
public record RequestLanding(Aircraft aircraft) implements TowerRequest {

    public RequestLanding {
        Objects.requireNonNull(aircraft, "aircraft");
    }
}
