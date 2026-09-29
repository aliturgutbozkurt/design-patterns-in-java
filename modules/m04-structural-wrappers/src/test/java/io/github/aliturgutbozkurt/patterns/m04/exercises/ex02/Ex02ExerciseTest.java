package io.github.aliturgutbozkurt.patterns.m04.exercises.ex02;

import java.time.Clock;
import java.time.Duration;
import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m04-structural-wrappers test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected CachingWeatherService cache(WeatherService target, Duration ttl, int maxEntries, Clock clock) {
        return new TtlCachingWeatherService(target, ttl, maxEntries, clock);
    }
}
