package io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater;

import java.util.Objects;

/**
 * Subsystem: the projector.
 *
 * @see "m05 lesson, section Facade"
 */
public final class Projector {

    private final ActionLog log;

    public Projector(ActionLog log) {
        this.log = Objects.requireNonNull(log, "log");
    }

    public void on() {
        log.record("projector", "on");
    }

    public void wideScreenMode() {
        log.record("projector", "widescreen mode");
    }

    public void off() {
        log.record("projector", "off");
    }
}
