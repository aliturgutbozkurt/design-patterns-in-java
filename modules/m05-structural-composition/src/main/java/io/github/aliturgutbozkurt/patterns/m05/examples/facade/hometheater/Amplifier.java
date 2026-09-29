package io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater;

import java.util.Objects;

/**
 * Subsystem: the amplifier. Knows nothing about the facade.
 *
 * @see "m05 lesson, section Facade"
 */
public final class Amplifier {

    private final ActionLog log;

    public Amplifier(ActionLog log) {
        this.log = Objects.requireNonNull(log, "log");
    }

    public void on() {
        log.record("amplifier", "on");
    }

    public void setInput(String input) {
        log.record("amplifier", "input " + input);
    }

    public void setSurroundSound() {
        log.record("amplifier", "surround sound");
    }

    public void setVolume(int level) {
        log.record("amplifier", "volume " + level);
    }

    public void off() {
        log.record("amplifier", "off");
    }
}
