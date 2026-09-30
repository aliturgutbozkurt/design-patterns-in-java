package io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow;

import java.util.Locale;
import java.util.Objects;

/**
 * A reading converted to °F — the output item type of the {@link CelsiusToFahrenheit} processor stage.
 *
 * @see "m07 lesson, section Observer — back-pressure with Flow"
 */
public record FahrenheitReading(String sensor, int sequence, double fahrenheit) {

    public FahrenheitReading {
        Objects.requireNonNull(sensor, "sensor");
    }

    /** Short form used in the demo output, e.g. {@code greenhouse#1 68.0F}. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s#%d %.1fF", sensor, sequence, fahrenheit);
    }
}
