package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.scaling;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Facts about the two kinds of thread, observed from inside a running thread: a {@code Thread.ofVirtual()} thread
 * is always a daemon thread and has no name unless the builder gives it one.
 *
 * @see "m10 lesson, section Thread-per-task with virtual threads"
 */
public final class ThreadKinds {

    /** What a thread reports about itself. */
    public record Facts(String name, boolean virtual, boolean daemon) {}

    private ThreadKinds() {}

    /** A factory of virtual threads named {@code prefix0}, {@code prefix1}, … in creation order. */
    public static ThreadFactory namedVirtual(String prefix) {
        return Thread.ofVirtual().name(prefix, 0).factory();
    }

    /** Starts one thread from {@code builder}, lets it describe itself, and waits for it to end. */
    public static Facts describe(Thread.Builder builder) throws InterruptedException {
        var facts = new AtomicReference<Facts>();
        Thread thread = builder.start(() -> {
            Thread self = Thread.currentThread();
            facts.set(new Facts(self.getName(), self.isVirtual(), self.isDaemon()));
        });
        thread.join();                                  // join() also makes the write above visible here
        return facts.get();
    }
}
