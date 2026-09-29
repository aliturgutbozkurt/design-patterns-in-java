package io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater;

import java.util.ArrayList;
import java.util.List;

/**
 * Records what the subsystems did, in order, so tests and the demo can see every call the facade makes.
 *
 * @see "m05 lesson, section Facade"
 */
public final class ActionLog {

    private final List<String> entries = new ArrayList<>();

    public void record(String device, String action) {
        entries.add(device + ": " + action);
    }

    /** A snapshot of the entries so far. */
    public List<String> entries() {
        return List.copyOf(entries);
    }

    public void clear() {
        entries.clear();
    }
}
