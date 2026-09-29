package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.caching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CachingProxyTest {

    private static final Duration TTL = Duration.ofMinutes(10);

    private final SlowExchangeRateService remote = new SlowExchangeRateService(Map.of(
            "EUR/TRY", new BigDecimal("48.10"),
            "USD/TRY", new BigDecimal("41.25")));
    private final ManualClock clock = new ManualClock(Instant.parse("2026-09-29T09:00:00Z"));
    private final ExchangeRateService rates = new CachingExchangeRateService(remote, TTL, clock);

    @Test
    void firstCallGoesToTheService() {
        assertThat(rates.rate("EUR", "TRY")).isEqualByComparingTo("48.10");
        assertThat(remote.calls()).isEqualTo(1);
    }

    @Test
    void repeatWithinTheTtlDoesNotCallTheService() {
        rates.rate("EUR", "TRY");
        clock.advance(TTL.minusNanos(1));
        assertThat(rates.rate("EUR", "TRY")).isEqualByComparingTo("48.10");
        assertThat(remote.calls()).isEqualTo(1);
    }

    @Test
    void entryIsStaleExactlyAtStoredAtPlusTtl() {
        rates.rate("EUR", "TRY");
        clock.advance(TTL);
        rates.rate("EUR", "TRY");
        assertThat(remote.calls()).isEqualTo(2);
        clock.advance(TTL.minusNanos(1));                      // the refreshed entry counts from its new storedAt
        rates.rate("EUR", "TRY");
        assertThat(remote.calls()).isEqualTo(2);
    }

    @Test
    void differentCurrencyPairsAreCachedSeparately() {
        assertThat(rates.rate("EUR", "TRY")).isEqualByComparingTo("48.10");
        assertThat(rates.rate("USD", "TRY")).isEqualByComparingTo("41.25");
        rates.rate("EUR", "TRY");
        rates.rate("USD", "TRY");
        assertThat(remote.calls()).isEqualTo(2);
    }

    @Test
    void rejectsInvalidConfiguration() {
        assertThatIllegalArgumentException().isThrownBy(() -> new CachingExchangeRateService(remote, Duration.ZERO, clock))
                .withMessage("ttl must be positive: PT0S");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new CachingExchangeRateService(remote, Duration.ofSeconds(-1), clock));
        assertThatNullPointerException().isThrownBy(() -> new CachingExchangeRateService(null, TTL, clock));
        assertThatNullPointerException().isThrownBy(() -> new CachingExchangeRateService(remote, TTL, null));
    }

    @Test
    void manualClockMovesOnlyWhenAdvanced() {
        var manual = new ManualClock(Instant.EPOCH);
        assertThat(manual.instant()).isEqualTo(Instant.EPOCH);
        manual.advance(Duration.ofSeconds(5));
        assertThat(manual.instant()).isEqualTo(Instant.ofEpochSecond(5));
        assertThat(manual.getZone()).isEqualTo(ZoneOffset.UTC);
        assertThat(manual.withZone(ZoneOffset.ofHours(3)).instant()).isEqualTo(Instant.ofEpochSecond(5));
    }

    @Test
    void demoPrintsRatesAndRemoteCallCounts() {
        assertThat(Console.capture(() -> CachingProxyDemo.main(new String[0]))).isEqualTo("""
                2026-09-29T09:00:00Z EUR/TRY = 48.10  (remote calls: 1)
                2026-09-29T09:09:00Z EUR/TRY = 48.10  (remote calls: 1)
                2026-09-29T09:09:00Z USD/TRY = 41.25  (remote calls: 2)
                2026-09-29T09:10:00Z EUR/TRY = 48.10  (remote calls: 3)
                2026-09-29T09:10:00Z USD/TRY = 41.25  (remote calls: 3)
                """);
    }
}
