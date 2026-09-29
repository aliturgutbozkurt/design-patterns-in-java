package io.github.aliturgutbozkurt.patterns.m04.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. A weather forecast for one city. */
public record Forecast(String city, int temperatureCelsius, String summary) {

    public Forecast {
        Objects.requireNonNull(city, "city");
        Objects.requireNonNull(summary, "summary");
    }
}
