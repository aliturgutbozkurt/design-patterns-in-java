package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.glyphs;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Flyweight factory: hands out one shared {@link Glyph} per (symbol, font, size). {@code computeIfAbsent} on a
 * {@link ConcurrentHashMap} runs the creating function at most once per key, so it is safe from many threads.
 * The cache is an instance field, not a static one: whoever needs sharing owns a factory.
 *
 * @see "m05 lesson, section Flyweight"
 */
public final class GlyphFactory {

    private record Key(char symbol, String font, int size) {}

    private final ConcurrentMap<Key, Glyph> cache = new ConcurrentHashMap<>();
    private final AtomicInteger created = new AtomicInteger();

    public Glyph glyph(char symbol, String font, int size) {
        return cache.computeIfAbsent(new Key(symbol, font, size), key -> {
            var glyph = new Glyph(key.symbol(), key.font(), key.size());   // runs at most once per key
            created.incrementAndGet();
            return glyph;
        });
    }

    /** How many glyph objects this factory has created so far. */
    public int created() {
        return created.get();
    }
}
