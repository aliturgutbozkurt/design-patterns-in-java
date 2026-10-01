package io.github.aliturgutbozkurt.patterns.m09.examples.lazy.memo;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Lazy initialisation and memoisation as functions: the idea of the Virtual Proxy and the lazy holder, with no new
 * class per use. Each cache lives inside the returned function, never in a static field.
 *
 * @see "m09 lesson, section Lazy evaluation and memoisation"
 */
public final class Memoized {

    private Memoized() {}

    /**
     * A thread-safe supplier that calls {@code source} at most once <em>successfully</em>. An exception is rethrown
     * and not cached (the next {@code get()} tries again); a {@code null} result is rejected.
     */
    public static <T> Supplier<T> supplier(Supplier<? extends T> source) {
        return new OnceSupplier<>(Objects.requireNonNull(source, "source"));
    }

    /**
     * A function that calls {@code f} once per distinct key ({@link ConcurrentHashMap#computeIfAbsent}). Only for
     * non-recursive {@code f}: a function that calls its own memoised version must not use this.
     */
    public static <K, V> Function<K, V> function(Function<? super K, ? extends V> f) {
        Objects.requireNonNull(f, "f");
        Map<K, V> cache = new ConcurrentHashMap<>();
        return key -> cache.computeIfAbsent(key, f);
    }

    private static final class OnceSupplier<T> implements Supplier<T> {

        private final Supplier<? extends T> source;
        private volatile T value;

        OnceSupplier(Supplier<? extends T> source) {
            this.source = source;
        }

        @Override
        public T get() {
            T result = value;
            if (result == null) {
                synchronized (this) {
                    result = value;
                    if (result == null) {
                        result = Objects.requireNonNull(source.get(), "the supplier returned null");
                        value = result;
                    }
                }
            }
            return result;
        }
    }
}
