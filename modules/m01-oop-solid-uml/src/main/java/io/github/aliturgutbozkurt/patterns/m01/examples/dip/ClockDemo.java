package io.github.aliturgutbozkurt.patterns.m01.examples.dip;

import io.github.aliturgutbozkurt.patterns.m01.examples.dip.clock.Session;
import io.github.aliturgutbozkurt.patterns.m01.examples.dip.clock.SessionPolicy;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

/** Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/dip/ClockDemo.java} */
public final class ClockDemo {

    private ClockDemo() {}

    public static void main(String[] args) {
        var start = Instant.parse("2026-09-29T09:00:00Z");
        var timeout = Duration.ofMinutes(30);
        Session session = new SessionPolicy(Clock.fixed(start, ZoneOffset.UTC), timeout).start("ada");
        System.out.println("session for " + session.user() + " started at " + session.startedAt() + ", timeout " + timeout);

        for (Instant now : List.of(start.plus(timeout).minusSeconds(1), start.plus(timeout))) {
            var policy = new SessionPolicy(Clock.fixed(now, ZoneOffset.UTC), timeout);  // a pinned clock per moment
            System.out.println("at " + now + " -> " + (policy.isExpired(session) ? "expired" : "active"));
        }
    }
}
