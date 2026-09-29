package io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Prototype registry: configured units are stored once under a name and every {@link #spawn} hands out a copy.
 *
 * @see "m03 lesson, section Prototype"
 */
public final class UnitRegistry {

    private final Map<String, Unit> prototypes = new TreeMap<>();

    /** Stores a <em>copy</em>, so later changes to {@code prototype} do not leak into the registry. */
    public void register(String name, Unit prototype) {
        prototypes.put(name, prototype.copy());
    }

    public Unit spawn(String name) {
        Unit prototype = prototypes.get(name);
        if (prototype == null) {
            throw new IllegalArgumentException("unknown unit: " + name + " (known: " + names() + ")");
        }
        return prototype.copy();
    }

    public List<String> names() {
        return List.copyOf(prototypes.keySet());
    }
}
