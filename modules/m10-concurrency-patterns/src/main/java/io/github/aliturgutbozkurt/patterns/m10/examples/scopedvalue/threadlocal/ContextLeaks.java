package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.threadlocal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * The {@code ThreadLocal} leak, made deterministic: a pool with a single thread runs task 1 and then task 2 on
 * the same thread, so whatever task 1 left behind, task 2 sees.
 *
 * @see "m10 lesson, section Scoped Values"
 */
public final class ContextLeaks {

    private ContextLeaks() {}

    /** Task 1 sets the user and forgets {@code remove()}; returns what task 2 sees. */
    public static String secondTaskSeesWithoutRemove() throws InterruptedException, ExecutionException {
        var context = new ThreadLocalContext();
        try (var pool = Executors.newSingleThreadExecutor()) {
            pool.submit(() -> context.set("alice")).get();         // request of alice, no cleanup
            return pool.submit(context::get).get();                // request of someone else
        }
    }

    /** Task 1 cleans up in {@code finally}; returns what task 2 sees. */
    public static String secondTaskSeesWithFinallyRemove() throws InterruptedException, ExecutionException {
        var context = new ThreadLocalContext();
        try (var pool = Executors.newSingleThreadExecutor()) {
            pool.submit(() -> {
                context.set("alice");
                try {
                    return "handled";
                } finally {
                    context.remove();                              // easy to forget, and nothing reminds you
                }
            }).get();
            return pool.submit(context::get).get();
        }
    }
}
