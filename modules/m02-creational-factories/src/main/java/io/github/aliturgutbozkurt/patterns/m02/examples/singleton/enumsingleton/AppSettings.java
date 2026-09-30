package io.github.aliturgutbozkurt.patterns.m02.examples.singleton.enumsingleton;

import java.util.Map;

/**
 * Singleton as a one-element {@code enum}: the JVM creates {@code INSTANCE} exactly once, thread-safely, and neither
 * serialization nor reflection can make a second one.
 *
 * @see "m02 lesson, section Singleton"
 */
public enum AppSettings {
    INSTANCE;

    private final Map<String, String> values = Map.of(
            "app.name", "PatternShop",
            "currency", "EUR",
            "page.size", "20");

    /** The value of a setting; unknown keys are an error, not {@code null}. */
    public String get(String key) {
        String value = values.get(key);
        if (value == null) {
            throw new IllegalArgumentException("unknown setting: " + key);
        }
        return value;
    }

    public int pageSize() {
        return Integer.parseInt(get("page.size"));
    }
}
