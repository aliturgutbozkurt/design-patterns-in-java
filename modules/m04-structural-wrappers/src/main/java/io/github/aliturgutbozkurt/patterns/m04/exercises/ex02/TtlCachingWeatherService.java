package io.github.aliturgutbozkurt.patterns.m04.exercises.ex02;

import java.time.Clock;
import java.time.Duration;

/** Assignment 02 — see assignments/02-weather-cache.en.md (Türkçe: 02-weather-cache.tr.md). */
public class TtlCachingWeatherService implements CachingWeatherService {

    public TtlCachingWeatherService(WeatherService target, Duration ttl, int maxEntries, Clock clock) {
        // TODO(ex02): validate (ttl > 0, maxEntries >= 1, no nulls) and keep the collaborators.
    }

    @Override
    public Forecast forecast(String city) {
        throw new UnsupportedOperationException("TODO(ex02): answer from the cache while fresh, else ask the target");
    }

    @Override
    public void invalidate(String city) {
        throw new UnsupportedOperationException("TODO(ex02): remove one city from the cache");
    }

    @Override
    public CacheStats stats() {
        throw new UnsupportedOperationException("TODO(ex02): return the hits and misses so far");
    }
}
