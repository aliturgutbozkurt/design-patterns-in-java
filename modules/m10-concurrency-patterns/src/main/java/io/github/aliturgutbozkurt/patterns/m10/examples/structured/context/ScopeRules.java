package io.github.aliturgutbozkurt.patterns.m10.examples.structured.context;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The rules that keep a scope's structure intact, each shown by breaking it: only the owner thread (the one that
 * opened the scope) may fork and join, forking ends at {@code join()}, results exist only after {@code join()}, and
 * a scope with forks must be joined before it is closed. Each method returns the exception the API threw.
 * Preview API (JEP 533).
 *
 * @see "m10 lesson, section Structured Concurrency"
 */
public final class ScopeRules {

    private ScopeRules() {}

    /** {@code fork} from a thread that does not own the scope. */
    public static RuntimeException forkFromAnotherThread() throws InterruptedException {
        var thrown = new AtomicReference<RuntimeException>();
        try (var scope = StructuredTaskScope.open()) {
            Thread intruder = Thread.ofVirtual().start(() -> {
                try {
                    scope.fork(() -> "not allowed");
                } catch (WrongThreadException e) {
                    thrown.set(e);
                }
            });
            intruder.join();
            joinQuietly(scope);
        }
        return required(thrown.get(), "fork from another thread");
    }

    /** {@code fork} after {@code join}. */
    public static RuntimeException forkAfterJoin() throws InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            scope.fork(() -> "first");
            joinQuietly(scope);
            try {
                scope.fork(() -> "too late");
            } catch (IllegalStateException e) {
                return e;
            }
        }
        return required(null, "fork after join");
    }

    /** {@code Subtask.get()} before {@code join}. */
    public static RuntimeException getBeforeJoin() throws InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            var subtask = scope.fork(() -> "result");
            RuntimeException thrown = null;
            try {
                subtask.get();
            } catch (IllegalStateException e) {
                thrown = e;
            }
            joinQuietly(scope);
            return required(thrown, "get before join");
        }
    }

    /** Leaving the {@code try} block after forking, without {@code join}. */
    public static RuntimeException closeWithoutJoin() {
        try (var scope = StructuredTaskScope.open()) {
            scope.fork(() -> "forgotten");
        } catch (IllegalStateException e) {        // thrown by close()
            return e;
        }
        return required(null, "close without join");
    }

    private static void joinQuietly(StructuredTaskScope<Object, Void, ExecutionException> scope)
            throws InterruptedException {
        try {
            scope.join();
        } catch (ExecutionException e) {
            throw new IllegalStateException("a trivial subtask failed", e);
        }
    }

    private static RuntimeException required(RuntimeException thrown, String rule) {
        if (thrown == null) {
            throw new IllegalStateException("the API did not reject: " + rule);
        }
        return thrown;
    }
}
