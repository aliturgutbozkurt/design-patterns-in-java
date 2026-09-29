package io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Flow;

/**
 * A slow {@link Flow.Subscriber}: it requests nothing by itself, only when its owner calls {@link #request(long)} —
 * the consumer that falls behind and makes the publisher's buffer fill up. It records every signal it sees.
 *
 * @param <T> item type
 * @see "m07 lesson, section Observer — back-pressure with Flow"
 */
public final class ManualSubscriber<T> implements Flow.Subscriber<T> {

    private final List<T> received = new CopyOnWriteArrayList<>();
    private final List<String> signals = new CopyOnWriteArrayList<>();
    private volatile Flow.Subscription subscription;
    private volatile Throwable error;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        signals.add("onSubscribe");
    }

    @Override
    public void onNext(T item) {
        received.add(item);
        signals.add("onNext " + item);
    }

    @Override
    public void onError(Throwable error) {
        this.error = error;
        signals.add("onError " + error.getClass().getSimpleName());
    }

    @Override
    public void onComplete() {
        signals.add("onComplete");
    }

    /** Asks for {@code n} more items; {@code n <= 0} is a protocol violation answered with {@code onError}. */
    public void request(long n) {
        if (subscription == null) {
            throw new IllegalStateException("not subscribed yet");
        }
        signals.add("request(" + n + ")");
        subscription.request(n);
    }

    public List<T> received() {
        return List.copyOf(received);
    }

    public List<String> signals() {
        return List.copyOf(signals);
    }

    /** The error signalled by the publisher, if any. */
    public Optional<Throwable> error() {
        return Optional.ofNullable(error);
    }
}
