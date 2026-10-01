package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask;

import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.scaling.ThreadKinds;
import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.scaling.Workloads;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/threadpertask/ThreadPerTaskDemo.java}
 *
 * <p>10 000 blocking tasks: a pool of 100 platform threads lets only 100 of them wait at once, one virtual thread
 * per task lets all 10 000 wait at once. The jobs meet at a gate instead of sleeping, so the numbers are exact.
 *
 * @see "m10 lesson, section Thread-per-task with virtual threads"
 */
public final class ThreadPerTaskDemo {

    private ThreadPerTaskDemo() {}

    public static void main(String[] args) throws InterruptedException {
        var pooled = Workloads.runOnFixedPool(100, 10_000);
        System.out.println("10000 blocking tasks on a fixed pool of 100 platform threads: completed "
                + pooled.completed() + ", peak in flight " + pooled.peakInFlight());
        var perTask = Workloads.runThreadPerTask(10_000);
        System.out.println("10000 blocking tasks, one virtual thread each: completed "
                + perTask.completed() + ", peak in flight " + perTask.peakInFlight());

        System.out.println("Thread.ofPlatform(): "
                + ThreadKinds.describe(Thread.ofPlatform().daemon(false).name("worker-", 0)));
        System.out.println("Thread.ofVirtual():  " + ThreadKinds.describe(Thread.ofVirtual().name("crawler-", 0)));
        System.out.println("unnamed virtual thread has name \"\": "
                + ThreadKinds.describe(Thread.ofVirtual()).name().isEmpty());
    }
}
