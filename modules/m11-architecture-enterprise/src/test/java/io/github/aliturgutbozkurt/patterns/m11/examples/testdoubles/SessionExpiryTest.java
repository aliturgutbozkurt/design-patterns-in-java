package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.time.MutableClock;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.time.SequenceTokens;
import io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.time.SessionManager;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class SessionExpiryTest {

    private static final Instant START = Instant.parse("2026-09-30T10:00:00Z");
    private static final Duration TTL = Duration.ofMinutes(30);

    private final MutableClock clock = new MutableClock(START, ZoneOffset.UTC);
    private final SessionManager sessions = new SessionManager(clock, TTL, new SequenceTokens());

    @Test
    void sessionIsValidJustBeforeTheTtlAndExpiredAtExactlyTheTtl() {
        String token = sessions.login("ada");
        clock.advance(TTL.minusMillis(1));
        assertThat(sessions.isValid(token)).isTrue();
        clock.advance(Duration.ofMillis(1));
        assertThat(sessions.isValid(token)).isFalse();
        assertThat(sessions.userOf(token)).isEmpty();
    }

    @Test
    void touchExtendsTheExpiry() {
        String token = sessions.login("ada");
        clock.advance(Duration.ofMinutes(20));
        sessions.touch(token);
        clock.advance(Duration.ofMinutes(29));
        assertThat(sessions.userOf(token)).contains("ada");
        clock.advance(Duration.ofMinutes(1));
        assertThatIllegalStateException().isThrownBy(() -> sessions.touch(token))
                .withMessage("session expired: token-1");
    }

    @Test
    void fixedClockAndMutableClockAgreeWhenTimeDoesNotMove() {
        var fixed = new SessionManager(Clock.fixed(START, ZoneOffset.UTC), TTL, new SequenceTokens());
        String a = fixed.login("ada");
        String b = sessions.login("ada");
        assertThat(fixed.isValid(a)).isEqualTo(sessions.isValid(b)).isTrue();
        assertThat(fixed.userOf(a)).isEqualTo(sessions.userOf(b));
    }

    @Test
    void tokensAreASequence() {
        assertThat(sessions.login("ada")).isEqualTo("token-1");
        assertThat(sessions.login("alan")).isEqualTo("token-2");
        assertThat(sessions.isValid("token-9")).isFalse();
    }

    @Test
    void withZoneSharesTheSameInstant() {
        Clock istanbul = clock.withZone(ZoneId.of("Europe/Istanbul"));
        clock.advance(Duration.ofHours(1));
        assertThat(istanbul.instant()).isEqualTo(START.plus(Duration.ofHours(1)));
        assertThat(istanbul.getZone()).isEqualTo(ZoneId.of("Europe/Istanbul"));
        ((MutableClock) istanbul).advance(Duration.ofMinutes(5));
        assertThat(clock.instant()).isEqualTo(START.plus(Duration.ofMinutes(65)));
    }

    @Test
    void demoPrintsExpiryToTheMillisecond() {
        assertThat(Console.capture(() -> SessionExpiryDemo.main(new String[0]))).isEqualTo("""
                2026-09-30T10:00:00Z ada logged in: token-1
                2026-09-30T10:29:59.999Z valid: true
                2026-09-30T10:30:00Z valid: false (expired at exactly the ttl)
                2026-09-30T11:10:00Z token-2 touched after 20 min, valid 20 min later: true
                """);
    }
}
