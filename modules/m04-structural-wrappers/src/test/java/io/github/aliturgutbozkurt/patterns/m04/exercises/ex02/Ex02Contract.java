package io.github.aliturgutbozkurt.patterns.m04.exercises.ex02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.time.Clock;
import java.time.Duration;
import org.junit.jupiter.api.Test;

/** Assignment 02 — caching proxy with TTL for a slow weather service. Each test is one acceptance criterion. */
public abstract class Ex02Contract {

    protected abstract CachingWeatherService cache(WeatherService target, Duration ttl, int maxEntries, Clock clock);

    private static final Duration TTL = Duration.ofMinutes(30);

    private final CountingWeatherService service = new CountingWeatherService();
    private final MutableClock clock = new MutableClock();

    private CachingWeatherService cache(int maxEntries) {
        return cache(service, TTL, maxEntries, clock);
    }

    @Test
    void firstCallGoesToTheService() {
        Forecast forecast = cache(10).forecast("Ankara");
        assertThat(forecast.city()).isEqualTo("ankara");
        assertThat(service.calls()).isEqualTo(1);
    }

    @Test
    void repeatWithinTtlIsServedFromCache() {
        var cache = cache(10);
        Forecast first = cache.forecast("Ankara");
        clock.advance(TTL.minusSeconds(1));
        assertThat(cache.forecast("Ankara")).isEqualTo(first);
        assertThat(service.calls()).isEqualTo(1);
    }

    @Test
    void entryExpiresExactlyAtTtl() {
        var cache = cache(10);
        Forecast first = cache.forecast("Ankara");
        clock.advance(TTL);
        Forecast second = cache.forecast("Ankara");
        assertThat(service.calls()).isEqualTo(2);
        assertThat(second).isNotEqualTo(first);
        clock.advance(TTL.minusNanos(1));                      // fresh again, counted from the new store time
        assertThat(cache.forecast("Ankara")).isEqualTo(second);
        assertThat(service.calls()).isEqualTo(2);
    }

    @Test
    void cityKeysIgnoreCaseAndSurroundingSpaces() {
        var cache = cache(10);
        cache.forecast("Ankara");
        cache.forecast("  ankara ");
        cache.forecast("ANKARA");
        assertThat(service.calls()).isEqualTo(1);
    }

    @Test
    void differentCitiesAreCachedSeparately() {
        var cache = cache(10);
        assertThat(cache.forecast("Ankara").city()).isEqualTo("ankara");
        assertThat(cache.forecast("Izmir").city()).isEqualTo("izmir");
        cache.forecast("Ankara");
        cache.forecast("Izmir");
        assertThat(service.calls()).isEqualTo(2);
    }

    @Test
    void failuresAreNotCached() {
        var cache = cache(10);
        var outage = new IllegalStateException("weather service down");
        service.failNextCallWith(outage);
        assertThat(catchThrowable(() -> cache.forecast("Izmir"))).isSameAs(outage);   // propagates unchanged
        Forecast recovered = cache.forecast("Izmir");
        assertThat(service.calls()).isEqualTo(2);
        assertThat(cache.forecast("Izmir")).isEqualTo(recovered);
        assertThat(service.calls()).isEqualTo(2);
    }

    @Test
    void invalidateForcesARefresh() {
        var cache = cache(10);
        cache.forecast("Ankara");
        cache.invalidate(" ANKARA ");
        cache.forecast("Ankara");
        assertThat(service.calls()).isEqualTo(2);
        cache.invalidate("Atlantis");                           // unknown city: no-op
        cache.forecast("Ankara");
        assertThat(service.calls()).isEqualTo(2);
    }

    @Test
    void statsCountHitsAndMisses() {
        var cache = cache(10);
        assertThat(cache.stats()).isEqualTo(new CacheStats(0, 0));
        cache.forecast("Ankara");                               // miss
        cache.forecast("ankara");                               // hit
        cache.forecast("Izmir");                                // miss
        service.failNextCallWith(new IllegalStateException("down"));
        catchThrowable(() -> cache.forecast("Bursa"));          // miss, even though it failed
        cache.forecast("Izmir");                                // hit
        assertThat(cache.stats()).isEqualTo(new CacheStats(2, 3));
    }

    @Test
    void leastRecentlyUsedEntryIsEvictedWhenFull() {
        var cache = cache(2);
        cache.forecast("Ankara");
        cache.forecast("Izmir");
        cache.forecast("Ankara");                               // hit: Ankara is now the most recently used
        cache.forecast("Bursa");                                // full: evicts Izmir
        assertThat(service.calls()).isEqualTo(3);
        cache.forecast("Ankara");
        cache.forecast("Bursa");
        assertThat(service.calls()).isEqualTo(3);
        cache.forecast("Izmir");                                // was evicted
        assertThat(service.calls()).isEqualTo(4);
    }

    @Test
    void rejectsInvalidConfiguration() {
        assertThatIllegalArgumentException().isThrownBy(() -> cache(service, Duration.ZERO, 10, clock));
        assertThatIllegalArgumentException().isThrownBy(() -> cache(service, Duration.ofSeconds(-1), 10, clock));
        assertThatIllegalArgumentException().isThrownBy(() -> cache(service, TTL, 0, clock));
        assertThatNullPointerException().isThrownBy(() -> cache(null, TTL, 10, clock));
        assertThatNullPointerException().isThrownBy(() -> cache(service, TTL, 10, null));
    }

    @Test
    void rejectsBlankCityWithoutCallingTheService() {
        var cache = cache(10);
        assertThatIllegalArgumentException().isThrownBy(() -> cache.forecast(""));
        assertThatIllegalArgumentException().isThrownBy(() -> cache.forecast("   "));
        assertThat(service.calls()).isZero();
    }

    @Test
    void usableWhereAWeatherServiceIsExpected() {
        WeatherService weather = cache(10);
        assertThat(morningBriefing(weather, "Ankara")).startsWith("ankara: 21 °C");
        assertThat(morningBriefing(weather, "Ankara")).startsWith("ankara: 21 °C");
        assertThat(service.calls()).isEqualTo(1);
    }

    private static String morningBriefing(WeatherService weather, String city) {     // client code: knows no cache
        Forecast forecast = weather.forecast(city);
        return forecast.city() + ": " + forecast.temperatureCelsius() + " °C, " + forecast.summary();
    }
}
