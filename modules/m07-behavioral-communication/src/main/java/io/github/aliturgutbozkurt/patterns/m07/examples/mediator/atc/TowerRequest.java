package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.atc;

/**
 * Every message an {@link Aircraft} can send to the {@link ControlTower}: a closed set, so the tower handles it with
 * one exhaustive {@code switch}.
 *
 * @see "m07 lesson, section Mediator — Modern Java 27"
 */
public sealed interface TowerRequest permits RequestLanding, RequestTakeoff, DeclareEmergency, RunwayVacated {

    /** The sender. */
    Aircraft aircraft();
}
