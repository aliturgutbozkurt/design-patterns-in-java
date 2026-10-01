package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.threadlocal;

/**
 * The classic way to carry request context: a {@link ThreadLocal}. It is mutable (anyone can {@code set} it), it
 * lives as long as the thread, and the caller must remember {@code remove()}, or the value survives into the next
 * task that runs on the same pooled thread.
 *
 * @see "m10 lesson, section Scoped Values"
 */
public final class ThreadLocalContext {

    private final ThreadLocal<String> user = new ThreadLocal<>();

    public void set(String name) {
        user.set(name);
    }

    /** The current thread's user, or {@code null}. */
    public String get() {
        return user.get();
    }

    public void remove() {
        user.remove();
    }
}
