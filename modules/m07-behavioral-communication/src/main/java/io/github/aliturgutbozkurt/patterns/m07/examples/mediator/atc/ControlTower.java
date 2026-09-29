package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.atc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Mediator for an airport with one runway: it owns the shared resource and the waiting queue, so no aircraft has to
 * know about any other. Every request is handled by one exhaustive {@code switch} over {@link TowerRequest}.
 *
 * @see "m07 lesson, section Mediator — Modern Java 27"
 */
public final class ControlTower {

    /** An aircraft waiting for the runway and the clearance it will get. */
    private record Waiting(Aircraft aircraft, String clearance) {}

    private final Consumer<String> radio;
    private final Deque<Waiting> queue = new ArrayDeque<>();
    private Aircraft onRunway; // null while the runway is free

    /** {@code radio} hears every request and every answer (a log). */
    public ControlTower(Consumer<String> radio) {
        this.radio = Objects.requireNonNull(radio, "radio");
    }

    public void handle(TowerRequest request) {
        radio.accept(request.aircraft() + " -> tower: " + request.getClass().getSimpleName());
        switch (request) {
            case RequestLanding(var aircraft) -> useRunwayOrWait(aircraft, "cleared to land");
            case RequestTakeoff(var aircraft) -> useRunwayOrWait(aircraft, "cleared for takeoff");
            case DeclareEmergency(var aircraft) -> {
                queue.removeIf(waiting -> waiting.aircraft() == aircraft);
                if (onRunway == null) {
                    clear(aircraft, "cleared for emergency landing");
                } else {
                    queue.addFirst(new Waiting(aircraft, "cleared for emergency landing")); // jumps the queue
                    transmit(aircraft, "emergency acknowledged, you are number 1");
                }
            }
            case RunwayVacated(var aircraft) -> {
                if (onRunway != aircraft) {
                    throw new IllegalStateException(aircraft + " is not on the runway");
                }
                onRunway = null;
                Waiting next = queue.pollFirst();
                if (next != null) {
                    clear(next.aircraft(), next.clearance());
                }
            }
        }
    }

    /** The aircraft currently cleared for the runway, if any. */
    public Optional<Aircraft> runwayOccupant() {
        return Optional.ofNullable(onRunway);
    }

    private void useRunwayOrWait(Aircraft aircraft, String clearance) {
        if (onRunway == null) {
            clear(aircraft, clearance);
        } else {
            queue.addLast(new Waiting(aircraft, clearance));
            transmit(aircraft, "hold, you are number " + queue.size());
        }
    }

    private void clear(Aircraft aircraft, String clearance) {
        onRunway = aircraft;
        transmit(aircraft, clearance);
    }

    private void transmit(Aircraft aircraft, String message) {
        radio.accept("tower -> " + aircraft + ": " + message);
        aircraft.receive(message);
    }
}
