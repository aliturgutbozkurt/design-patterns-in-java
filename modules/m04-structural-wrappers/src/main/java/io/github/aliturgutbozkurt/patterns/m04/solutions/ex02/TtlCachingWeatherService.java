package io.github.aliturgutbozkurt.patterns.m04.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m04.exercises.ex02.CacheStats;
import io.github.aliturgutbozkurt.patterns.m04.exercises.ex02.CachingWeatherService;
import io.github.aliturgutbozkurt.patterns.m04.exercises.ex02.Forecast;
import io.github.aliturgutbozkurt.patterns.m04.exercises.ex02.WeatherService;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Reference solution for assignment 02: a caching proxy with a time-to-live, statistics and an LRU size bound.
 * Single-threaded by design.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class TtlCachingWeatherService implements CachingWeatherService {

    private record Entry(Forecast forecast, Instant storedAt) {}

    private final WeatherService target;
    private final Duration ttl;
    private final Clock clock;
    private final Map<String, Entry> entries;
    private long hits;
    private long misses;

    public TtlCachingWeatherService(WeatherService target, Duration ttl, int maxEntries, Clock clock) {
        this.target = Objects.requireNonNull(target, "target");
        this.clock = Objects.requireNonNull(clock, "clock");
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("ttl must be positive: " + ttl);
        }
        if (maxEntries < 1) {
            throw new IllegalArgumentException("maxEntries must be at least 1: " + maxEntries);
        }
        this.ttl = ttl;
        this.entries = new LruMap<>(maxEntries);
    }

    /** A map in access order that drops its least recently used entry when it grows past {@code maxEntries}. */
    private static final class LruMap<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1L;
        private final int maxEntries;

        LruMap(int maxEntries) {
            super(16, 0.75f, true);             // accessOrder = true: get() and put() move an entry to the end
            this.maxEntries = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxEntries;
        }
    }

    @Override
    public Forecast forecast(String city) {
        String key = key(city);
        Instant now = clock.instant();
        Entry entry = entries.get(key);
        if (entry != null && now.isBefore(entry.storedAt().plus(ttl))) {
            hits++;
            return entry.forecast();
        }
        misses++;
        Forecast forecast = target.forecast(city);             // an exception propagates and nothing is stored
        entries.put(key, new Entry(forecast, now));
        return forecast;
    }

    @Override
    public void invalidate(String city) {
        entries.remove(key(city));
    }

    @Override
    public CacheStats stats() {
        return new CacheStats(hits, misses);
    }

    private static String key(String city) {
        if (city.isBlank()) {
            throw new IllegalArgumentException("city must not be blank");
        }
        return city.strip().toLowerCase(Locale.ROOT);           // Locale.ROOT: the same key on every machine
    }
}
