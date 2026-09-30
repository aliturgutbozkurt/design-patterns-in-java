package io.github.aliturgutbozkurt.patterns.m05.examples.facade.hometheater;

import java.util.Objects;

/**
 * Subsystem: the streaming player.
 *
 * @see "m05 lesson, section Facade"
 */
public final class StreamingPlayer {

    private final ActionLog log;

    public StreamingPlayer(ActionLog log) {
        this.log = Objects.requireNonNull(log, "log");
    }

    public void on() {
        log.record("player", "on");
    }

    public void play(String title) {
        log.record("player", "play \"" + title + "\"");
    }

    public void stop() {
        log.record("player", "stop");
    }

    public void off() {
        log.record("player", "off");
    }
}
