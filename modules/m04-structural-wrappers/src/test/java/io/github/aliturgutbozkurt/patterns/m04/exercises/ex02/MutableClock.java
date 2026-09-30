package io.github.aliturgutbozkurt.patterns.m04.exercises.ex02;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/** Test-only clock that moves only when advanced. */
final class MutableClock extends Clock {

    private Instant now = Instant.parse("2026-10-01T08:00:00Z");

    void advance(Duration duration) {
        now = now.plus(duration);
    }

    @Override
    public Instant instant() {
        return now;
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        throw new UnsupportedOperationException("not needed by the tests");
    }
}
