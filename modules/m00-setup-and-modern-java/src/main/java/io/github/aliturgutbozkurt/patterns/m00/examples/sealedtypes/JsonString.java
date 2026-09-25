package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

import java.util.Objects;

/** A JSON string. */
public record JsonString(String value) implements Json {

    public JsonString {
        Objects.requireNonNull(value, "value");
    }
}
