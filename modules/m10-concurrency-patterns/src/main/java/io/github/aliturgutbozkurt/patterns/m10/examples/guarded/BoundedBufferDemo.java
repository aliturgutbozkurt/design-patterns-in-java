package io.github.aliturgutbozkurt.patterns.m10.examples.guarded;

import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.buffer.BoundedBuffer;
import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.buffer.LockConditionBuffer;
import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.buffer.MonitorBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/guarded/BoundedBufferDemo.java}
 *
 * <p>A sensor reader hands 8 readings to a writer through a buffer of 2. Both threads keep suspending at their
 * guards (reader: "full", writer: "empty"); FIFO order makes the result the same every time.
 *
 * @see "m10 lesson, section Guarded Suspension and Balking"
 */
public final class BoundedBufferDemo {

    private BoundedBufferDemo() {}

    public static void main(String[] args) throws InterruptedException {
        System.out.println("LockConditionBuffer, capacity 2: writer stored " + handOff(new LockConditionBuffer<>(2)));
        System.out.println("MonitorBuffer, capacity 2: writer stored " + handOff(new MonitorBuffer<>(2)));
        System.out.println("poll(50 ms) on an empty buffer: "
                + new LockConditionBuffer<String>(2).poll(Duration.ofMillis(50)));
    }

    private static List<String> handOff(BoundedBuffer<String> buffer) throws InterruptedException {
        List<String> stored = new ArrayList<>();
        Thread reader = Thread.ofVirtual().start(() -> {
            try {
                for (int i = 1; i <= 8; i++) {
                    buffer.put("r" + i);             // suspends while the buffer is full
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        for (int i = 0; i < 8; i++) {
            stored.add(buffer.take());               // suspends while the buffer is empty
        }
        reader.join();
        return stored;
    }
}
