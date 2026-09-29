package io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A {@link Flow.Publisher} of sensor readings backed by the JDK's {@link SubmissionPublisher}. Readings are
 * <em>offered</em>: a subscriber that has no demand left and a full buffer loses the reading, and the drop is
 * counted — the publisher never blocks.
 *
 * @see "m07 lesson, section Observer — back-pressure with Flow"
 */
public final class TemperatureFeed implements Flow.Publisher<Reading>, AutoCloseable {

    private final SubmissionPublisher<Reading> publisher;
    private final AtomicInteger dropped = new AtomicInteger();

    /** Delivers on {@code executor}; pass {@code Runnable::run} for synchronous, deterministic delivery. */
    public TemperatureFeed(Executor executor, int bufferSize) {
        this(new SubmissionPublisher<>(Objects.requireNonNull(executor, "executor"), bufferSize));
    }

    /** Delivers asynchronously on the default pool ({@code ForkJoinPool.commonPool()}). */
    public TemperatureFeed() {
        this(new SubmissionPublisher<>());
    }

    private TemperatureFeed(SubmissionPublisher<Reading> publisher) {
        this.publisher = publisher;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super Reading> subscriber) {
        publisher.subscribe(subscriber);
    }

    /** Offers {@code reading} to every subscriber; saturated subscribers drop it (never blocks). */
    public void publish(Reading reading) {
        publisher.offer(Objects.requireNonNull(reading, "reading"), (subscriber, item) -> {
            dropped.incrementAndGet();
            return false; // do not retry
        });
    }

    /** Subscribes a consumer that requests everything; the future completes after {@link #close()}. */
    public CompletableFuture<Void> consume(Consumer<? super Reading> consumer) {
        return publisher.consume(consumer);
    }

    /** How many (subscriber, reading) deliveries were dropped so far. */
    public int droppedCount() {
        return dropped.get();
    }

    /** Per-subscriber buffer size, rounded up by the JDK to a power of two. */
    public int bufferCapacity() {
        return publisher.getMaxBufferCapacity();
    }

    public int subscriberCount() {
        return publisher.getNumberOfSubscribers();
    }

    /** Signals {@code onError(error)} to every subscriber. */
    public void fail(Throwable error) {
        publisher.closeExceptionally(error);
    }

    /** Signals {@code onComplete} to every subscriber once its buffered readings were delivered. */
    @Override
    public void close() {
        publisher.close();
    }
}
