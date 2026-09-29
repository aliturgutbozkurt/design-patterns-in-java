package io.github.aliturgutbozkurt.patterns.m01.examples.dip.clock;

import java.time.Clock;
import java.time.Duration;
import java.util.Objects;

/**
 * DIP with a JDK abstraction: the policy asks an injected {@link Clock} for "now" instead of calling
 * {@code Instant.now()}, so tests can pin time to any instant.
 *
 * @see "m01 lesson, section DIP"
 */
public final class SessionPolicy {

    private final Clock clock;
    private final Duration timeout;

    public SessionPolicy(Clock clock, Duration timeout) {
        this.clock = Objects.requireNonNull(clock, "clock");
        Objects.requireNonNull(timeout, "timeout");
        if (timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("timeout must be positive: " + timeout);
        }
        this.timeout = timeout;
    }

    public Session start(String user) {
        return new Session(user, clock.instant());
    }

    /** Expired from the instant {@code startedAt + timeout} onwards. */
    public boolean isExpired(Session session) {
        return !clock.instant().isBefore(session.startedAt().plus(timeout));
    }
}
