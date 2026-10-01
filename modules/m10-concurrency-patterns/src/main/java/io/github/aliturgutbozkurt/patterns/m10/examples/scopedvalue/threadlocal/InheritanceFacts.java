package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.threadlocal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Which child threads see a parent's context. An {@link InheritableThreadLocal} is copied into every child thread;
 * a plain {@link ThreadLocal} is not. A {@link ScopedValue} is inherited only by structured subtasks
 * ({@code StructuredTaskScope}, see the {@code structured.context} example), never by executor tasks or raw threads.
 *
 * @see "m10 lesson, section Scoped Values"
 */
public final class InheritanceFacts {

    private static final ScopedValue<String> USER = ScopedValue.newInstance();

    private InheritanceFacts() {}

    /** Sets an {@code InheritableThreadLocal} and returns what a child virtual thread reads from it. */
    public static String inheritableSeenByChild(String user) throws InterruptedException {
        var inheritable = new InheritableThreadLocal<String>();
        inheritable.set(user);
        try {
            return readInChild(inheritable);
        } finally {
            inheritable.remove();
        }
    }

    /** Sets a plain {@code ThreadLocal} and returns what a child virtual thread reads from it. */
    public static String plainSeenByChild(String user) throws InterruptedException {
        var plain = new ThreadLocal<String>();
        plain.set(user);
        try {
            return readInChild(plain);
        } finally {
            plain.remove();
        }
    }

    /** Binds a scoped value and asks whether a task of a virtual-thread executor sees it. */
    public static boolean scopedValueBoundInExecutorTask() throws InterruptedException, ExecutionException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<Boolean> seen = ScopedValue.where(USER, "alice")
                    .call(() -> executor.submit(USER::isBound));     // submitted while USER is bound
            return seen.get();
        }
    }

    /** Binds a scoped value and asks whether a raw {@code Thread.ofVirtual()} thread sees it. */
    public static boolean scopedValueBoundInRawVirtualThread() throws InterruptedException {
        var bound = new AtomicBoolean(true);
        ScopedValue.where(USER, "alice").call(() -> {
            Thread child = Thread.ofVirtual().start(() -> bound.set(USER.isBound()));
            child.join();
            return null;
        });
        return bound.get();
    }

    /** Whether the value is still bound once {@code run} has returned. */
    public static boolean scopedValueBoundAfterRun() {
        ScopedValue.where(USER, "alice").run(() -> {});
        return USER.isBound();
    }

    private static String readInChild(ThreadLocal<String> local) throws InterruptedException {
        var seen = new AtomicReference<String>();
        Thread child = Thread.ofVirtual().start(() -> seen.set(local.get()));
        child.join();
        return seen.get();
    }
}
