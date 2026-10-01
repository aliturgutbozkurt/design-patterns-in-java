package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

/**
 * A JSON number (JSON has no separate integer type).
 *
 * @see "m00 lesson, section A recursive example: JSON"
 */
public record JsonNumber(double value) implements Json {

    public JsonNumber {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("JSON numbers must be finite: " + value);
        }
    }
}
