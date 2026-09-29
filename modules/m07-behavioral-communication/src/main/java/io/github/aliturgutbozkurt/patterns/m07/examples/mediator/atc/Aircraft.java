package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.atc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Colleague: an aircraft never talks to another aircraft, only to the tower, and records what the tower told it.
 *
 * @see "m07 lesson, section Mediator — Modern Java 27"
 */
public final class Aircraft {

    private final String callSign;
    private final ControlTower tower;
    private final List<String> inbox = new ArrayList<>();

    public Aircraft(String callSign, ControlTower tower) {
        this.callSign = Objects.requireNonNull(callSign, "callSign");
        this.tower = Objects.requireNonNull(tower, "tower");
    }

    public String callSign() {
        return callSign;
    }

    public void requestLanding() {
        tower.handle(new RequestLanding(this));
    }

    public void requestTakeoff() {
        tower.handle(new RequestTakeoff(this));
    }

    public void declareEmergency() {
        tower.handle(new DeclareEmergency(this));
    }

    public void vacateRunway() {
        tower.handle(new RunwayVacated(this));
    }

    void receive(String message) {
        inbox.add(message);
    }

    /** Everything the tower said to this aircraft, oldest first. */
    public List<String> inbox() {
        return List.copyOf(inbox);
    }

    @Override
    public String toString() {
        return callSign;
    }
}
