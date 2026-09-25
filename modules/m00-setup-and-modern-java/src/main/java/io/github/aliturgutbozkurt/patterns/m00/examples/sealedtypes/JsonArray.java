package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

import java.util.List;

/** A JSON array. The list is copied, so the record stays immutable even if the caller's list changes. */
public record JsonArray(List<Json> items) implements Json {

    public JsonArray {
        items = List.copyOf(items);
    }
}
