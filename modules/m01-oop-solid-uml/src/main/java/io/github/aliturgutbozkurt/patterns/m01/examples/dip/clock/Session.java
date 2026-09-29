package io.github.aliturgutbozkurt.patterns.m01.examples.dip.clock;

import java.time.Instant;
import java.util.Objects;

/**
 * A login session: who, and since when.
 *
 * @see "m01 lesson, section DIP"
 */
public record Session(String user, Instant startedAt) {

    public Session {
        Objects.requireNonNull(user, "user");
        Objects.requireNonNull(startedAt, "startedAt");
    }
}
