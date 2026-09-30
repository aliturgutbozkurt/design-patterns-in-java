package io.github.aliturgutbozkurt.patterns.m01.examples.dip;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m01.examples.dip.clock.Session;
import io.github.aliturgutbozkurt.patterns.m01.examples.dip.clock.SessionPolicy;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class SessionPolicyTest {

    static final Instant NINE = Instant.parse("2026-09-29T09:00:00Z");
    static final Duration THIRTY_MINUTES = Duration.ofMinutes(30);

    static SessionPolicy policyAt(Instant now) {
        return new SessionPolicy(Clock.fixed(now, ZoneOffset.UTC), THIRTY_MINUTES);
    }

    @Test
    void startsASessionAtTheClocksInstant() {
        assertThat(policyAt(NINE).start("ada")).isEqualTo(new Session("ada", NINE));
    }

    @Test
    void sessionIsActiveUntilJustBeforeTheTimeout() {
        var session = new Session("ada", NINE);
        assertThat(policyAt(NINE.plus(THIRTY_MINUTES).minusSeconds(1)).isExpired(session)).isFalse();
    }

    @Test
    void sessionExpiresExactlyAtTheTimeout() {
        var session = new Session("ada", NINE);
        assertThat(policyAt(NINE.plus(THIRTY_MINUTES)).isExpired(session)).isTrue();
    }

    @Test
    void worksWithAnOffsetClock() {
        var base = Clock.fixed(NINE, ZoneOffset.UTC);
        var policy = new SessionPolicy(Clock.offset(base, Duration.ofHours(1)), THIRTY_MINUTES);
        assertThat(policy.isExpired(new Session("ada", NINE))).isTrue();
    }

    @Test
    void rejectsNonPositiveTimeout() {
        var clock = Clock.fixed(NINE, ZoneOffset.UTC);
        assertThatIllegalArgumentException().isThrownBy(() -> new SessionPolicy(clock, Duration.ZERO));
    }

    @Test
    void demoPrintsTheBoundary() {
        assertThat(Console.capture(() -> ClockDemo.main(new String[0]))).isEqualTo("""
                session for ada started at 2026-09-29T09:00:00Z, timeout PT30M
                at 2026-09-29T09:29:59Z -> active
                at 2026-09-29T09:30:00Z -> expired
                """);
    }
}
