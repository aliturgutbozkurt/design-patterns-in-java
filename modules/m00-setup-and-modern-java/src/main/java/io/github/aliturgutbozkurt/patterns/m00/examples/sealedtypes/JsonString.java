package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

import java.util.Objects;

/**
 * A JSON string.
 *
 * @see "m00 lesson, section A recursive example: JSON"
 */
public record JsonString(String value) implements Json {

    public JsonString {
        Objects.requireNonNull(value, "value");
    }
}
