package io.github.aliturgutbozkurt.patterns.m04.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m04.exercises.ex02.CachingWeatherService;
import io.github.aliturgutbozkurt.patterns.m04.exercises.ex02.Ex02Contract;
import io.github.aliturgutbozkurt.patterns.m04.exercises.ex02.WeatherService;
import java.time.Clock;
import java.time.Duration;

class Ex02SolutionTest extends Ex02Contract {

    @Override
    protected CachingWeatherService cache(WeatherService target, Duration ttl, int maxEntries, Clock clock) {
        return new TtlCachingWeatherService(target, ttl, maxEntries, clock);
    }
}
