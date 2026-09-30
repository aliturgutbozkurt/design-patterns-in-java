package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment.FulfilmentPipeline;
import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment.Order;
import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment.Shipment;
import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment.Stage;
import io.github.aliturgutbozkurt.patterns.m10.support.Await;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class FulfilmentPipelineTest {

    private static final Stage INSTANT = _ -> {};

    private static Order order(long id) {
        return new Order(id, List.of("item-" + id));
    }

    @Test
    void everyOrderPassesAllStagesInOrder() throws InterruptedException {
        var pipeline = new FulfilmentPipeline(4, INSTANT, INSTANT, INSTANT, Thread.ofVirtual().factory());
        for (long id = 1; id <= 50; id++) {
            pipeline.submit(order(id));
        }
        assertThat(pipeline.shutdownAndAwait(Await.BOUND)).isTrue();
        assertThat(pipeline.shipments()).extracting(Shipment::orderId)
                .containsExactlyElementsOf(LongStream.rangeClosed(1, 50).boxed().toList());
        assertThat(pipeline.shipments()).extracting(Shipment::stageLog).containsOnly(List.of("pick", "pack", "ship"));
    }

    @Test
    void shutdownForwardsExactlyOnePillPerStageAndAllStageThreadsEnd() throws InterruptedException {
        List<Thread> threads = new CopyOnWriteArrayList<>();
        ThreadFactory recording = task -> {
            Thread thread = Thread.ofVirtual().unstarted(task);
            threads.add(thread);
            return thread;
        };
        var pipeline = new FulfilmentPipeline(2, INSTANT, INSTANT, INSTANT, recording);
        pipeline.submit(order(1));
        assertThat(pipeline.shutdownAndAwait(Await.BOUND)).isTrue();
        assertThat(pipeline.shutdownLog()).containsExactly("pick drained", "pack drained", "ship drained");
        assertThat(threads).hasSize(3).allMatch(thread -> thread.getState() == Thread.State.TERMINATED);
    }

    @Test
    void trySubmitReturnsFalseOnceAllQueuesAreFullAndEverythingShipsAfterTheGateOpens() throws Exception {
        var gate = new CountDownLatch(1);
        Stage gatedShip = _ -> Await.latch(gate);
        var pipeline = new FulfilmentPipeline(1, INSTANT, INSTANT, gatedShip, Thread.ofVirtual().factory());
        for (long id = 1; id <= FulfilmentPipeline.maxInFlight(1); id++) {
            pipeline.submit(order(id));     // 3 queues × 1 slot + 3 stages holding one order each = 6
        }
        assertThat(pipeline.trySubmit(order(7), Duration.ofMillis(50))).isFalse();   // nothing can move

        gate.countDown();
        pipeline.submit(order(7));
        assertThat(pipeline.shutdownAndAwait(Await.BOUND)).isTrue();
        assertThat(pipeline.shipments()).extracting(Shipment::orderId).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L);
    }

    @Test
    void shutdownAndAwaitReturnsFalseWhileAStageIsStalledAndTrueOnceItMoves() throws Exception {
        var gate = new CountDownLatch(1);
        Stage gatedShip = _ -> Await.latch(gate);
        var pipeline = new FulfilmentPipeline(1, INSTANT, INSTANT, gatedShip, Thread.ofVirtual().factory());
        pipeline.submit(order(1));
        assertThat(pipeline.shutdownAndAwait(Duration.ofMillis(50))).isFalse();     // ship still holds order 1
        assertThatThrownBy(() -> pipeline.submit(order(2))).isInstanceOf(IllegalStateException.class);

        gate.countDown();
        assertThat(pipeline.shutdownAndAwait(Await.BOUND)).isTrue();
        assertThat(pipeline.shipments()).extracting(Shipment::orderId).containsExactly(1L);
        assertThat(pipeline.shutdownLog()).hasSize(3);
    }

    @Test
    void trySubmitAcceptsWhileThereIsRoom() throws InterruptedException {
        var pipeline = new FulfilmentPipeline(1, INSTANT, INSTANT, INSTANT, Thread.ofVirtual().factory());
        assertThat(pipeline.trySubmit(order(1), Await.BOUND)).isTrue();
        assertThat(pipeline.shutdownAndAwait(Await.BOUND)).isTrue();
        assertThat(pipeline.shipments()).hasSize(1);
    }

    @Test
    void submitAfterShutdownIsRejected() throws InterruptedException {
        var pipeline = new FulfilmentPipeline(1, INSTANT, INSTANT, INSTANT, Thread.ofVirtual().factory());
        assertThat(pipeline.shutdownAndAwait(Await.BOUND)).isTrue();
        assertThatThrownBy(() -> pipeline.submit(order(1))).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> pipeline.trySubmit(order(2), Duration.ZERO)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void ordersAndShipmentsAreImmutableValues() {
        var items = new ArrayList<>(List.of("book"));
        var order = new Order(1, items);
        items.add("pen");
        assertThat(order.items()).containsExactly("book");
        assertThatThrownBy(() -> new Order(0, List.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void demoShowsBackPressureAndOrderedShipments() {
        assertThat(Demos.output(() -> FulfilmentDemo.main(new String[0]))).isEqualTo("""
                pipeline pick -> pack -> ship, 1 slot per queue, ship stage stalled
                submitted orders 1..6: every queue and every stage now holds one order
                trySubmit(order 7, 50 ms) -> false (back-pressure reached the producer)
                ship stage resumes
                submit(order 7) -> accepted
                shutdown: [pick drained, pack drained, ship drained]
                Shipment[orderId=1, stageLog=[pick, pack, ship]]
                Shipment[orderId=2, stageLog=[pick, pack, ship]]
                Shipment[orderId=3, stageLog=[pick, pack, ship]]
                Shipment[orderId=4, stageLog=[pick, pack, ship]]
                Shipment[orderId=5, stageLog=[pick, pack, ship]]
                Shipment[orderId=6, stageLog=[pick, pack, ship]]
                Shipment[orderId=7, stageLog=[pick, pack, ship]]
                """);
    }
}
