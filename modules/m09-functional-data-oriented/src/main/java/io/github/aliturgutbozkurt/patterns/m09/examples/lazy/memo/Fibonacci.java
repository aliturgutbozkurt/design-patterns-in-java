package io.github.aliturgutbozkurt.patterns.m09.examples.lazy.memo;

import java.util.HashMap;
import java.util.Map;

/**
 * The memoisation trap next to a correct version. {@link #recursiveWithComputeIfAbsent} changes the map from inside
 * its own {@code computeIfAbsent}, which {@link HashMap} forbids.
 *
 * @see "m09 lesson, section Lazy evaluation and memoisation"
 */
public final class Fibonacci {

    private final Map<Integer, Long> cache = new HashMap<>();

    /** BROKEN on purpose: throws {@link java.util.ConcurrentModificationException} for {@code n >= 3}. */
    public long recursiveWithComputeIfAbsent(int n) {
        if (n < 2) {
            return n;
        }
        return cache.computeIfAbsent(n, k -> recursiveWithComputeIfAbsent(k - 1) + recursiveWithComputeIfAbsent(k - 2));
    }

    /** Correct and needs no cache: keep only the last two values. */
    public static long iterative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must not be negative: " + n);
        }
        long previous = 0;
        long current = n == 0 ? 0 : 1;
        for (int i = 2; i <= n; i++) {
            long next = previous + current;
            previous = current;
            current = next;
        }
        return current;
    }
}
