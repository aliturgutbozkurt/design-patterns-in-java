package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A JSON value as a recursive sealed hierarchy — a tree whose leaves and branches share one type. This shape returns
 * in m05 (Composite) and m08 (Interpreter/Visitor).
 *
 * @see "m00 lesson, section A recursive example: JSON"
 */
public sealed interface Json permits JsonNull, JsonBool, JsonNumber, JsonString, JsonArray, JsonObject {

    /** Renders compact JSON text. One exhaustive switch handles every kind of value. */
    static String render(Json json) {
        return switch (json) {
            case JsonNull _ -> "null";
            case JsonBool(boolean value) -> String.valueOf(value);
            case JsonNumber(double value) when value == Math.rint(value) && !Double.isInfinite(value) ->
                    String.valueOf((long) value);
            case JsonNumber(double value) -> String.valueOf(value);
            case JsonString(String value) -> quote(value);
            case JsonArray(List<Json> items) -> items.stream().map(Json::render).collect(joining(",", "[", "]"));
            case JsonObject(Map<String, Json> members) -> members.entrySet().stream()
                    .map(member -> quote(member.getKey()) + ":" + render(member.getValue()))
                    .collect(joining(",", "{", "}"));
        };
    }

    /** Nested record patterns: "an object whose member {@code key} is a string". */
    static Optional<String> stringAt(Json json, String key) {
        if (json instanceof JsonObject(var members) && members.get(key) instanceof JsonString(String value)) {
            return Optional.of(value);
        }
        return Optional.empty();
    }

    private static String quote(String text) {
        StringBuilder out = new StringBuilder("\"");
        for (char ch : text.toCharArray()) {
            switch (ch) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\t' -> out.append("\\t");
                default -> out.append(ch);
            }
        }
        return out.append('"').toString();
    }
}
