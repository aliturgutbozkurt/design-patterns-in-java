package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Flyweight factory: one {@link TreeType} per distinct (species, colour, texture), thread-safe via
 * {@code computeIfAbsent}.
 *
 * @see "m05 lesson, section Flyweight"
 */
public final class TreeTypeFactory {

    private record Key(String species, String colour, String texture) {}

    private final ConcurrentMap<Key, TreeType> cache = new ConcurrentHashMap<>();
    private final AtomicInteger created = new AtomicInteger();

    public TreeType typeOf(String species, String colour, String texture) {
        return cache.computeIfAbsent(new Key(species, colour, texture), key -> {
            var type = new TreeType(key.species(), key.colour(), key.texture());
            created.incrementAndGet();
            return type;
        });
    }

    /** How many tree types this factory has created so far. */
    public int created() {
        return created.get();
    }
}
