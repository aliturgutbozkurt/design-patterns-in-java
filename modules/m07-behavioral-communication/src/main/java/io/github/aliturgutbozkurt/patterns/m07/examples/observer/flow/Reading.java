package io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow;

import java.util.Locale;
import java.util.Objects;

/**
 * One temperature reading in °C from an IoT sensor, the item type of the {@link TemperatureFeed}.
 *
 * @see "m07 lesson, section Observer — back-pressure with Flow"
 */
public record Reading(String sensor, int sequence, double celsius) {

    public Reading {
        Objects.requireNonNull(sensor, "sensor");
    }

    /** Short form used in the demo output, e.g. {@code greenhouse#1 20.0C}. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s#%d %.1fC", sensor, sequence, celsius);
    }
}
