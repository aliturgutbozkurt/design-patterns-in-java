package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** A JSON object. Members keep their insertion order and are copied into an unmodifiable map. */
public record JsonObject(Map<String, Json> members) implements Json {

    public JsonObject {
        members = Collections.unmodifiableMap(new LinkedHashMap<>(members));
    }
}
