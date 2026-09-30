package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.caching;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * A clock that only moves when told to, so demos and tests control time exactly.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class ManualClock extends Clock {

    private Instant now;
    private final ZoneId zone;

    public ManualClock(Instant start) {
        this(start, ZoneOffset.UTC);
    }

    private ManualClock(Instant start, ZoneId zone) {
        this.now = Objects.requireNonNull(start, "start");
        this.zone = zone;
    }

    public void advance(Duration duration) {
        now = now.plus(duration);
    }

    @Override
    public Instant instant() {
        return now;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId newZone) {
        return new ManualClock(now, newZone);
    }
}
