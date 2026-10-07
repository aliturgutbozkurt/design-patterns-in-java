package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/** Test clock: starts at {@link #START} and moves only when a test calls {@link #advance}. Thread-safe. */
public final class MutableClock extends Clock {

    /** 2026-11-16 09:00 in Istanbul, a Monday. */
    public static final ZonedDateTime START = ZonedDateTime.parse("2026-11-16T09:00+03:00[Europe/Istanbul]");

    private final AtomicReference<Instant> now;
    private final ZoneId zone;

    public MutableClock() {
        this(new AtomicReference<>(START.toInstant()), START.getZone());
    }

    private MutableClock(AtomicReference<Instant> now, ZoneId zone) {
        this.now = now;
        this.zone = zone;
    }

    /** Moves the time forward. */
    public void advance(Duration duration) {
        if (duration.isNegative()) {
            throw new IllegalArgumentException("time only moves forward: " + duration);
        }
        now.updateAndGet(instant -> instant.plus(duration));
    }

    /** Moves the time forward to {@code time}. */
    public void advanceTo(ZonedDateTime time) {
        advance(Duration.between(now.get(), time.toInstant()));
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId newZone) {
        return new MutableClock(now, Objects.requireNonNull(newZone, "zone"));
    }

    @Override
    public Instant instant() {
        return now.get();
    }
}
