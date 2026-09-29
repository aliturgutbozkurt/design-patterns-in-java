package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.virtual;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Thread-safe memoizer: computes its value on the first {@link #get()} and at most once, however many threads ask.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class Lazy<T> implements Supplier<T> {

    private final Supplier<? extends T> factory;
    private volatile T value;                                   // volatile: a published value is fully visible

    private Lazy(Supplier<? extends T> factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public static <T> Lazy<T> of(Supplier<? extends T> factory) {
        return new Lazy<>(factory);
    }

    @Override
    public T get() {
        T result = value;
        if (result == null) {                                   // fast path: no lock once initialised
            synchronized (this) {
                result = value;
                if (result == null) {                           // re-check: another thread may have won
                    result = Objects.requireNonNull(factory.get(), "factory returned null");
                    value = result;
                }
            }
        }
        return result;
    }

    public boolean isInitialized() {
        return value != null;
    }
}
