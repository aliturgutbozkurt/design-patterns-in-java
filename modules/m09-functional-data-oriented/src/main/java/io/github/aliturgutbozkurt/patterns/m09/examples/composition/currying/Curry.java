package io.github.aliturgutbozkurt.patterns.m09.examples.composition.currying;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Currying (one argument at a time) and partial application (fix some arguments now, the rest later).
 *
 * @see "m09 lesson, section Function composition, currying and partial application"
 */
public final class Curry {

    private Curry() {}

    /** {@code (a, b) -> c} becomes {@code a -> b -> c}. */
    public static <A, B, C> Function<A, Function<B, C>> curry(BiFunction<A, B, C> f) {
        return a -> b -> f.apply(a, b);
    }

    /** {@code a -> b -> c} becomes {@code (a, b) -> c}. */
    public static <A, B, C> BiFunction<A, B, C> uncurry(Function<A, Function<B, C>> f) {
        return (a, b) -> f.apply(a).apply(b);
    }

    /** Fixes the first argument. */
    public static <A, B, C> Function<B, C> partial(BiFunction<A, B, C> f, A a) {
        return b -> f.apply(a, b);
    }

    public static <A, B, C> BiFunction<B, A, C> flip(BiFunction<A, B, C> f) {
        return (b, a) -> f.apply(a, b);
    }
}
