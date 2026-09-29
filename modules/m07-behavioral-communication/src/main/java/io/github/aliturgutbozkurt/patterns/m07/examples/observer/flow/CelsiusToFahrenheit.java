package io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow;

import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * A processor stage: subscriber of {@link Reading}s upstream, publisher of {@link FahrenheitReading}s downstream. It
 * requests one reading at a time and uses {@code submit}, which blocks while the downstream buffer is full — that is
 * how back-pressure travels upstream. Completion and errors are passed on.
 *
 * @see "m07 lesson, section Observer — back-pressure with Flow"
 */
public final class CelsiusToFahrenheit extends SubmissionPublisher<FahrenheitReading>
        implements Flow.Processor<Reading, FahrenheitReading> {

    private Flow.Subscription upstream;

    public CelsiusToFahrenheit(Executor executor, int bufferSize) {
        super(executor, bufferSize);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        upstream = subscription;
        upstream.request(1);
    }

    @Override
    public void onNext(Reading reading) {
        submit(new FahrenheitReading(reading.sensor(), reading.sequence(), reading.celsius() * 9 / 5 + 32));
        upstream.request(1);
    }

    @Override
    public void onError(Throwable error) {
        closeExceptionally(error);
    }

    @Override
    public void onComplete() {
        close();
    }
}
