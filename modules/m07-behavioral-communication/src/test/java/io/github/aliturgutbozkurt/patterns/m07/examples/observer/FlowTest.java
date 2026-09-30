package io.github.aliturgutbozkurt.patterns.m07.examples.observer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow.BatchSubscriber;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow.CelsiusToFahrenheit;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow.FahrenheitReading;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow.ManualSubscriber;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow.Reading;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow.TemperatureFeed;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** All tests but one use the caller-runs executor {@code Runnable::run}: delivery is synchronous and deterministic. */
class FlowTest {

    private static Reading reading(int sequence) {
        return new Reading("s", sequence, 20.0 + sequence);
    }

    private static List<Reading> readings(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(FlowTest::reading).toList();
    }

    @Test
    void signalsOnSubscribeThenEveryItemInOrderThenOnCompleteAfterClose() {
        var subscriber = new BatchSubscriber<Reading>(10);
        var feed = new TemperatureFeed(Runnable::run, 4);
        feed.subscribe(subscriber);
        assertThat(subscriber.signals()).containsExactly("onSubscribe", "request(10)");
        readings(3).forEach(feed::publish);
        assertThat(subscriber.received()).containsExactlyElementsOf(readings(3));
        assertThat(subscriber.isCompleted()).isFalse();
        feed.close();
        assertThat(subscriber.isCompleted()).isTrue();
        assertThat(subscriber.signals()).containsExactly("onSubscribe", "request(10)",
                "onNext s#1 21.0C", "onNext s#2 22.0C", "onNext s#3 23.0C", "onComplete");
    }

    @ParameterizedTest(name = "{0} items in batches of {1} -> {2} requests")
    @CsvSource({"5, 2, 3", "7, 3, 3", "1, 4, 1", "6, 3, 3"})
    void batchSubscriberRequestsOnSubscribeAndAfterEveryFullBatch(int items, int batchSize, int expectedRequests) {
        // ceil(items / batchSize) whenever items is not a multiple of batchSize; floor(items / batchSize) + 1 in general
        var subscriber = new BatchSubscriber<Reading>(batchSize);
        try (var feed = new TemperatureFeed(Runnable::run, 4)) {
            feed.subscribe(subscriber);
            readings(items).forEach(feed::publish);
        }
        assertThat(subscriber.requestCount()).isEqualTo(expectedRequests).isEqualTo(items / batchSize + 1);
        assertThat(subscriber.received()).containsExactlyElementsOf(readings(items));
    }

    @Test
    void fullBufferWithoutDemandMakesOfferDropAndTheDropCountIsExact() {
        var slow = new ManualSubscriber<Reading>();
        var feed = new TemperatureFeed(Runnable::run, 2);
        feed.subscribe(slow);
        readings(5).forEach(feed::publish);
        assertThat(feed.bufferCapacity()).isEqualTo(2);
        assertThat(feed.droppedCount()).isEqualTo(3);
        assertThat(slow.received()).isEmpty();

        slow.request(5);
        assertThat(slow.received()).containsExactlyElementsOf(readings(2));
    }

    @Test
    void bufferCapacityIsRoundedUpToAPowerOfTwo() {
        assertThat(new TemperatureFeed(Runnable::run, 5).bufferCapacity()).isEqualTo(8);
    }

    @Test
    void bufferedItemsAreDeliveredBeforeOnCompleteEvenAfterClose() {
        var slow = new ManualSubscriber<Reading>();
        var feed = new TemperatureFeed(Runnable::run, 4);
        feed.subscribe(slow);
        readings(2).forEach(feed::publish);
        feed.close();
        assertThat(slow.signals()).containsExactly("onSubscribe");
        slow.request(1);
        slow.request(1);
        assertThat(slow.signals()).containsExactly("onSubscribe",
                "request(1)", "onNext s#1 21.0C", "request(1)", "onNext s#2 22.0C", "onComplete");
    }

    @Test
    void exceptionInOnNextBecomesOnErrorAndRemovesTheSubscriber() {
        var boom = new IllegalStateException("cannot store reading");
        List<Throwable> errors = new ArrayList<>();
        var failing = new Flow.Subscriber<Reading>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Reading item) {
                throw boom;
            }

            @Override
            public void onError(Throwable error) {
                errors.add(error);
            }

            @Override
            public void onComplete() {
                errors.add(new AssertionError("unexpected onComplete"));
            }
        };
        var feed = new TemperatureFeed(Runnable::run, 4);
        feed.subscribe(failing);
        feed.subscribe(new BatchSubscriber<>(1));
        assertThat(feed.subscriberCount()).isEqualTo(2);
        feed.publish(reading(1));
        assertThat(errors).containsExactly(boom);
        assertThat(feed.subscriberCount()).isEqualTo(1);
    }

    @Test
    void requestZeroIsAProtocolViolationSignalledAsIllegalArgumentException() {
        var subscriber = new ManualSubscriber<Reading>();
        new TemperatureFeed(Runnable::run, 4).subscribe(subscriber);
        subscriber.request(0);
        assertThat(subscriber.error()).containsInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void failSignalsOnErrorToSubscribers() {
        var subscriber = new BatchSubscriber<Reading>(2);
        var feed = new TemperatureFeed(Runnable::run, 4);
        feed.subscribe(subscriber);
        feed.fail(new IllegalStateException("sensor offline"));
        assertThat(subscriber.signals())
                .containsExactly("onSubscribe", "request(2)", "onError IllegalStateException: sensor offline");
    }

    @Test
    void processorConvertsCelsiusToFahrenheitAndPassesCompletionOn() {
        var downstream = new BatchSubscriber<FahrenheitReading>(2);
        try (var feed = new TemperatureFeed(Runnable::run, 4);
                var toFahrenheit = new CelsiusToFahrenheit(Runnable::run, 4)) {
            feed.subscribe(toFahrenheit);
            toFahrenheit.subscribe(downstream);
            feed.publish(new Reading("s", 1, 20.0));
            feed.publish(new Reading("s", 2, -40.0));
            feed.publish(new Reading("s", 3, 100.0));
        }
        assertThat(downstream.received()).containsExactly(
                new FahrenheitReading("s", 1, 68.0),
                new FahrenheitReading("s", 2, -40.0),
                new FahrenheitReading("s", 3, 212.0));
        assertThat(downstream.isCompleted()).isTrue();
    }

    /** The only asynchronous test: default pool, waits on the future of consume(...), asserts only what arrived. */
    @Test
    void deliversEverythingAsynchronouslyOnTheDefaultPool() throws Exception {
        List<Reading> received = new CopyOnWriteArrayList<>();
        var feed = new TemperatureFeed();
        var done = feed.consume(received::add);
        readings(5).forEach(feed::publish);
        feed.close();
        done.get(5, TimeUnit.SECONDS);
        assertThat(received).containsExactlyElementsOf(readings(5));
        assertThat(feed.droppedCount()).isZero();
    }

    @Test
    void demoPrintsBatchesDropsProcessorOutputAndFailure() {
        assertThat(Console.capture(() -> FlowDemo.main(new String[0]))).isEqualTo("""
                == 1. demand in batches of 2
                  onSubscribe
                  request(2)
                  onNext greenhouse#1 20.0C
                  onNext greenhouse#2 22.5C
                  request(2)
                  onNext greenhouse#3 25.0C
                  onNext greenhouse#4 17.5C
                  request(2)
                  onNext greenhouse#5 20.0C
                  onComplete
                == 2. a slow subscriber and a buffer of 2
                  fast received 5, slow received 0, dropped 3
                  slow: onSubscribe
                  slow: request(5)
                  slow: onNext greenhouse#1 20.0C
                  slow: onNext greenhouse#2 22.5C
                  slow: onComplete
                == 3. processor stage C -> F
                  greenhouse#1 68.0F
                  greenhouse#2 72.5F
                  greenhouse#3 77.0F
                  greenhouse#4 63.5F
                  greenhouse#5 68.0F
                == 4. the sensor fails
                  onSubscribe
                  request(2)
                  onNext greenhouse#1 20.0C
                  onError IllegalStateException: sensor offline
                """);
    }
}
