package io.github.aliturgutbozkurt.patterns.m04.exercises.ex02;

/** GIVEN — do not modify. A weather service with a cache that can be inspected and invalidated. */
public interface CachingWeatherService extends WeatherService {

    /** Removes the cached forecast for {@code city}; an unknown city is a no-op. */
    void invalidate(String city);

    /** Hits and misses so far. */
    CacheStats stats();
}
