package io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Flow;

/**
 * A {@link Flow.Subscriber} that controls its own pace: it requests {@code batchSize} items on subscribe and the next
 * batch only after the current one has arrived. It records every signal it sees.
 *
 * @param <T> item type
 * @see "m07 lesson, section Observer — back-pressure with Flow"
 */
public final class BatchSubscriber<T> implements Flow.Subscriber<T> {

    private final int batchSize;
    private final List<T> received = new CopyOnWriteArrayList<>();
    private final List<String> signals = new CopyOnWriteArrayList<>();
    // Flow signals to one subscriber never overlap (the protocol serialises them); volatile only for visibility.
    private volatile Flow.Subscription subscription;
    private volatile int remainingInBatch;
    private volatile int requestCount;
    private volatile boolean completed;

    public BatchSubscriber(int batchSize) {
        if (batchSize < 1) {
            throw new IllegalArgumentException("batchSize must be positive: " + batchSize);
        }
        this.batchSize = batchSize;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        signals.add("onSubscribe");
        requestBatch();
    }

    @Override
    public void onNext(T item) {
        received.add(item);
        signals.add("onNext " + item);
        remainingInBatch--;
        if (remainingInBatch == 0) {
            requestBatch();
        }
    }

    @Override
    public void onError(Throwable error) {
        signals.add("onError " + error.getClass().getSimpleName() + ": " + error.getMessage());
    }

    @Override
    public void onComplete() {
        completed = true;
        signals.add("onComplete");
    }

    private void requestBatch() {
        remainingInBatch = batchSize;
        requestCount++;
        signals.add("request(" + batchSize + ")"); // logged first: with a caller-runs executor request() delivers at once
        subscription.request(batchSize);
    }

    public List<T> received() {
        return List.copyOf(received);
    }

    /** Every signal in order, e.g. {@code onSubscribe}, {@code request(2)}, {@code onNext …}, {@code onComplete}. */
    public List<String> signals() {
        return List.copyOf(signals);
    }

    /** How many times {@code request(batchSize)} was called. */
    public int requestCount() {
        return requestCount;
    }

    public boolean isCompleted() {
        return completed;
    }
}
