package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles;

import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.time.MutableClock;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.time.SequenceTokens;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.time.SessionManager;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/testdoubles/SessionExpiryDemo.java}
 *
 * @see "m11 lesson, section Test doubles"
 */
public final class SessionExpiryDemo {

    private SessionExpiryDemo() {}

    public static void main(String[] args) {
        var clock = new MutableClock(Instant.parse("2026-09-30T10:00:00Z"), ZoneOffset.UTC);
        var sessions = new SessionManager(clock, Duration.ofMinutes(30), new SequenceTokens());

        String ada = sessions.login("ada");
        System.out.println(clock.instant() + " ada logged in: " + ada);
        clock.advance(Duration.ofMinutes(30).minusMillis(1));
        System.out.println(clock.instant() + " valid: " + sessions.isValid(ada));
        clock.advance(Duration.ofMillis(1));
        System.out.println(clock.instant() + " valid: " + sessions.isValid(ada) + " (expired at exactly the ttl)");

        String alan = sessions.login("alan");
        clock.advance(Duration.ofMinutes(20));
        sessions.touch(alan);
        clock.advance(Duration.ofMinutes(20));
        System.out.println(clock.instant() + " " + alan + " touched after 20 min, valid 20 min later: "
                + sessions.isValid(alan));
    }
}
