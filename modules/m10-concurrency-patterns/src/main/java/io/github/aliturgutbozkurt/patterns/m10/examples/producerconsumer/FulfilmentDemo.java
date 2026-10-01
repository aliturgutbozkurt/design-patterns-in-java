package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer;

import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment.FulfilmentPipeline;
import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment.Order;
import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment.Stage;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/producerconsumer/FulfilmentDemo.java}
 *
 * <p>The ship stage waits for a latch, so the queues fill up deterministically: 6 orders fit, the 7th does not.
 *
 * @see "m10 lesson, section Producer–Consumer"
 */
public final class FulfilmentDemo {

    private FulfilmentDemo() {}

    public static void main(String[] args) throws InterruptedException {
        var courierArrived = new CountDownLatch(1);
        Stage instant = _ -> {};
        Stage ship = _ -> courierArrived.await();             // stalled until the latch opens
        var pipeline = new FulfilmentPipeline(1, instant, instant, ship, Thread.ofVirtual().factory());
        System.out.println("pipeline pick -> pack -> ship, 1 slot per queue, ship stage stalled");

        for (long id = 1; id <= FulfilmentPipeline.maxInFlight(1); id++) {
            pipeline.submit(new Order(id, List.of("item-" + id)));
        }
        System.out.println("submitted orders 1..6: every queue and every stage now holds one order");
        boolean accepted = pipeline.trySubmit(new Order(7, List.of("item-7")), Duration.ofMillis(50));
        System.out.println("trySubmit(order 7, 50 ms) -> " + accepted + " (back-pressure reached the producer)");

        System.out.println("ship stage resumes");
        courierArrived.countDown();
        pipeline.submit(new Order(7, List.of("item-7")));
        System.out.println("submit(order 7) -> accepted");

        if (!pipeline.shutdownAndAwait(Duration.ofSeconds(5))) {
            throw new IllegalStateException("pipeline did not stop");
        }
        System.out.println("shutdown: " + pipeline.shutdownLog());
        pipeline.shipments().forEach(System.out::println);
    }
}
