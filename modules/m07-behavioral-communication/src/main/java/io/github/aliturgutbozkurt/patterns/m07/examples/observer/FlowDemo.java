package io.github.aliturgutbozkurt.patterns.m07.examples.observer;

import io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow.BatchSubscriber;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow.CelsiusToFahrenheit;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow.ManualSubscriber;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow.Reading;
import io.github.aliturgutbozkurt.patterns.m07.examples.observer.flow.TemperatureFeed;

/**
 * Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/observer/FlowDemo.java}
 *
 * <p>Every publisher uses the caller-runs executor {@code Runnable::run}: delivery happens synchronously inside
 * {@code subscribe}, {@code publish}, {@code request} and {@code close}, so the output is deterministic.
 *
 * @see "m07 lesson, section Observer"
 */
public final class FlowDemo {

    private static final double[] CELSIUS = {20.0, 22.5, 25.0, 17.5, 20.0};

    private FlowDemo() {}

    public static void main(String[] args) {
        System.out.println("== 1. demand in batches of 2");
        var batches = new BatchSubscriber<Reading>(2);
        try (var feed = new TemperatureFeed(Runnable::run, 4)) {
            feed.subscribe(batches);
            publishAll(feed);
        }
        batches.signals().forEach(signal -> System.out.println("  " + signal));

        System.out.println("== 2. a slow subscriber and a buffer of 2");
        var fast = new BatchSubscriber<Reading>(2);
        var slow = new ManualSubscriber<Reading>();
        try (var feed = new TemperatureFeed(Runnable::run, 2)) {
            feed.subscribe(fast);
            feed.subscribe(slow);
            publishAll(feed);
            System.out.println("  fast received " + fast.received().size() + ", slow received "
                    + slow.received().size() + ", dropped " + feed.droppedCount());
            slow.request(5);
        }
        slow.signals().forEach(signal -> System.out.println("  slow: " + signal));

        System.out.println("== 3. processor stage C -> F");
        try (var feed = new TemperatureFeed(Runnable::run, 4);
                var toFahrenheit = new CelsiusToFahrenheit(Runnable::run, 4)) {
            feed.subscribe(toFahrenheit);
            toFahrenheit.consume(reading -> System.out.println("  " + reading));
            publishAll(feed);
        }

        System.out.println("== 4. the sensor fails");
        var failing = new BatchSubscriber<Reading>(2);
        var feed = new TemperatureFeed(Runnable::run, 4);
        feed.subscribe(failing);
        feed.publish(new Reading("greenhouse", 1, 20.0));
        feed.fail(new IllegalStateException("sensor offline"));
        failing.signals().forEach(signal -> System.out.println("  " + signal));
    }

    private static void publishAll(TemperatureFeed feed) {
        for (int i = 0; i < CELSIUS.length; i++) {
            feed.publish(new Reading("greenhouse", i + 1, CELSIUS[i]));
        }
    }
}
