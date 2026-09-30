package io.github.aliturgutbozkurt.patterns.m04.exercises.ex02;

import java.util.Locale;

/** Test-only slow service: counts calls, and fails the next call when told to. */
final class CountingWeatherService implements WeatherService {

    private int calls;
    private RuntimeException nextFailure;

    @Override
    public Forecast forecast(String city) {
        calls++;
        if (nextFailure != null) {
            RuntimeException failure = nextFailure;
            nextFailure = null;
            throw failure;
        }
        String name = city.strip().toLowerCase(Locale.ROOT);
        return new Forecast(name, 20 + calls, "sunny (call " + calls + ")");
    }

    void failNextCallWith(RuntimeException failure) {
        nextFailure = failure;
    }

    int calls() {
        return calls;
    }
}
