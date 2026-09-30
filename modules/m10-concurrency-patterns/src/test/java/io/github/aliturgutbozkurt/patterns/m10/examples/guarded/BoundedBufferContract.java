package io.github.aliturgutbozkurt.patterns.m10.examples.guarded;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.guarded.buffer.BoundedBuffer;
import io.github.aliturgutbozkurt.patterns.m10.support.Await;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** One set of tests for every {@link BoundedBuffer}: both implementations must behave the same. */
@Timeout(10)
abstract class BoundedBufferContract {

    protected abstract <T> BoundedBuffer<T> newBuffer(int capacity);

    /** A virtual thread that takes one element and completes {@code result} with it (or with the exception). */
    private static Thread taker(BoundedBuffer<String> buffer, CompletableFuture<String> result) {
        return Thread.ofVirtual().start(() -> {
            try {
                result.complete(buffer.take());
            } catch (InterruptedException e) {
                result.completeExceptionally(e);
            }
        });
    }

    @Test
    void takesElementsInFifoOrder() throws InterruptedException {
        BoundedBuffer<String> buffer = newBuffer(3);
        buffer.put("a");
        buffer.put("b");
        buffer.put("c");
        assertThat(List.of(buffer.take(), buffer.take(), buffer.take())).containsExactly("a", "b", "c");
    }

    @Test
    void takeOnEmptyBufferBlocksUntilAPutReleasesItWithThatElement() throws Exception {
        BoundedBuffer<String> buffer = newBuffer(2);
        var taken = new CompletableFuture<String>();
        Thread thread = taker(buffer, taken);
        Await.untilBlocked(thread);                     // really suspended at the guard, not finished
        assertThat(taken).isNotDone();

        buffer.put("reading-1");
        assertThat(taken.get(5, TimeUnit.SECONDS)).isEqualTo("reading-1");
    }

    @Test
    void putOnFullBufferBlocksUntilATakeMakesRoom() throws Exception {
        BoundedBuffer<String> buffer = newBuffer(1);
        buffer.put("first");
        var putDone = new CompletableFuture<Void>();
        Thread putter = Thread.ofVirtual().start(() -> {
            try {
                buffer.put("second");
                putDone.complete(null);
            } catch (InterruptedException e) {
                putDone.completeExceptionally(e);
            }
        });
        Await.untilBlocked(putter);
        assertThat(putDone).isNotDone();
        assertThat(buffer.size()).isEqualTo(1);

        assertThat(buffer.take()).isEqualTo("first");
        putDone.get(5, TimeUnit.SECONDS);
        assertThat(buffer.take()).isEqualTo("second");
    }

    @Test
    void pollOnEmptyBufferTimesOutWithEmptyOptional() throws InterruptedException {
        BoundedBuffer<String> buffer = newBuffer(2);
        assertThat(buffer.poll(Duration.ofMillis(50))).isEmpty();
    }

    @Test
    void pollReturnsAnAvailableElementAtOnce() throws InterruptedException {
        BoundedBuffer<String> buffer = newBuffer(2);
        buffer.put("x");
        assertThat(buffer.poll(Duration.ZERO)).isEqualTo(Optional.of("x"));
    }

    @Test
    void interruptingABlockedTakeThrowsInterruptedExceptionAndLosesNoElement() throws Exception {
        BoundedBuffer<String> buffer = newBuffer(2);
        var taken = new CompletableFuture<String>();
        Thread thread = taker(buffer, taken);
        Await.untilBlocked(thread);

        thread.interrupt();
        assertThatThrownBy(() -> taken.get(5, TimeUnit.SECONDS)).hasCauseInstanceOf(InterruptedException.class);
        Await.terminated(thread);

        buffer.put("kept");                             // the interrupted taker must not swallow it
        assertThat(buffer.size()).isEqualTo(1);
        assertThat(buffer.take()).isEqualTo("kept");
    }

    @Test
    void eightProducersAndEightConsumersMoveEveryItemExactlyOnce() throws Exception {
        BoundedBuffer<Integer> buffer = newBuffer(4);
        int producers = 8;
        int perProducer = 1_000;
        var taken = new ConcurrentLinkedQueue<Integer>();
        List<Future<?>> tasks = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int p = 0; p < producers; p++) {
                int first = p * perProducer;
                tasks.add(executor.submit(() -> {
                    for (int i = first; i < first + perProducer; i++) {
                        buffer.put(i);
                    }
                    return null;
                }));
                tasks.add(executor.submit(() -> {
                    for (int i = 0; i < perProducer; i++) {
                        taken.add(buffer.take());
                    }
                    return null;
                }));
            }
            for (Future<?> task : tasks) {
                task.get(5, TimeUnit.SECONDS);
            }
        }
        assertThat(taken).containsExactlyInAnyOrderElementsOf(
                IntStream.range(0, producers * perProducer).boxed().toList());
        assertThat(buffer.size()).isZero();
    }

    @Test
    void rejectsCapacityBelowOne() {
        assertThatThrownBy(() -> newBuffer(0)).isInstanceOf(IllegalArgumentException.class);
    }
}
