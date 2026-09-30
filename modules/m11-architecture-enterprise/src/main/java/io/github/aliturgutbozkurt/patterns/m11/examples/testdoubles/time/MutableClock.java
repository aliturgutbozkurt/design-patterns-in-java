package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.time;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Fake clock: a real {@link Clock} whose time moves only when a test calls {@link #advance}. Views created with
 * {@link #withZone} share the same instant — advancing one advances them all.
 *
 * @see "m11 lesson, section Test doubles — time"
 */
public final class MutableClock extends Clock {

    /** The shared, mutable "now" behind every zone view of this clock. */
    private static final class Now {
        private Instant instant;

        Now(Instant instant) {
            this.instant = instant;
        }
    }

    private final Now now;
    private final ZoneId zone;

    public MutableClock(Instant start, ZoneId zone) {
        this(new Now(Objects.requireNonNull(start, "start")), zone);
    }

    private MutableClock(Now now, ZoneId zone) {
        this.now = now;
        this.zone = Objects.requireNonNull(zone, "zone");
    }

    /** Moves time forward (or backward, for a negative duration). */
    public void advance(Duration duration) {
        now.instant = now.instant.plus(duration);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId newZone) {
        return new MutableClock(now, newZone);
    }

    @Override
    public Instant instant() {
        return now.instant;
    }
}
