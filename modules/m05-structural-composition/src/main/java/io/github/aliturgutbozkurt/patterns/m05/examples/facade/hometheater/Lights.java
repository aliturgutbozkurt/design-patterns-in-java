package io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater;

import java.util.Objects;

/**
 * Subsystem: the room lights.
 *
 * @see "m05 lesson, section Facade"
 */
public final class Lights {

    private final ActionLog log;

    public Lights(ActionLog log) {
        this.log = Objects.requireNonNull(log, "log");
    }

    public void dim(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be 0..100: " + percent);
        }
        log.record("lights", "dim to " + percent + "%");
    }

    public void on() {
        log.record("lights", "on");
    }
}
