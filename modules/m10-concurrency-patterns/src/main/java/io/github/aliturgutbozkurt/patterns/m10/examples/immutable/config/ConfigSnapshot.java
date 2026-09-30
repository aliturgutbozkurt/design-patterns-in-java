package io.github.aliturgutbozkurt.patterns.m10.examples.immutable.config;

import java.util.HashMap;
import java.util.Map;

/**
 * One immutable version of the configuration. Fields that belong together (the pool bounds) always change
 * together, because the only way to change them is to build a new snapshot.
 *
 * @see "m10 lesson, section Immutable Object"
 */
public record ConfigSnapshot(int version, int minConnections, int maxConnections, Map<String, String> flags) {

    public ConfigSnapshot {
        if (version < 0) {
            throw new IllegalArgumentException("version must not be negative: " + version);
        }
        if (minConnections < 0) {
            throw new IllegalArgumentException("minConnections must not be negative: " + minConnections);
        }
        if (minConnections > maxConnections) {
            throw new IllegalArgumentException(
                    "minConnections " + minConnections + " > maxConnections " + maxConnections);
        }
        flags = Map.copyOf(flags);
    }

    public ConfigSnapshot withVersion(int newVersion) {
        return new ConfigSnapshot(newVersion, minConnections, maxConnections, flags);
    }

    /** Both bounds at once: there is no moment in which only one of them has changed. */
    public ConfigSnapshot withPoolSize(int min, int max) {
        return new ConfigSnapshot(version, min, max, flags);
    }

    public ConfigSnapshot withFlag(String name, String value) {
        Map<String, String> next = new HashMap<>(flags);
        next.put(name, value);
        return new ConfigSnapshot(version, minConnections, maxConnections, next);
    }
}
