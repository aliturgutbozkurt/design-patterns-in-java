package io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater;

import java.util.Objects;

/**
 * Subsystem: the motorised screen.
 *
 * @see "m05 lesson, section Facade"
 */
public final class Screen {

    private final ActionLog log;

    public Screen(ActionLog log) {
        this.log = Objects.requireNonNull(log, "log");
    }

    public void down() {
        log.record("screen", "down");
    }

    public void up() {
        log.record("screen", "up");
    }
}
