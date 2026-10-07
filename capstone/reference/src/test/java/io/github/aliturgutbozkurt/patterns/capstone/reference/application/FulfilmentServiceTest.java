package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderShipped;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentFailure;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentReport;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryOrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.EventDispatcher;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.UnitOfWork;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.ShipmentOutcome;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.Warehouse;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Order;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderItem;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderLifecycle;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Transition;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class FulfilmentServiceTest {

    /** Warehouse double: records threads and concurrency, can meet at a barrier and fail chosen orders. */
    private static final class CountingWarehouse implements Warehouse {
        final AtomicInteger inFlight = new AtomicInteger();
        final AtomicInteger peak = new AtomicInteger();
        final List<Boolean> virtualThreads = new CopyOnWriteArrayList<>();
        final Set<OrderId> failing = ConcurrentHashMap.newKeySet();
        CyclicBarrier barrier;

        @Override
        public ShipmentOutcome ship(OrderId order, List<OrderItem> physicalItems, String postalCode) {
            peak.accumulateAndGet(inFlight.incrementAndGet(), Math::max);
            virtualThreads.add(Thread.currentThread().isVirtual());
            try {
                if (barrier != null) {
                    barrier.await(5, TimeUnit.SECONDS);
                }
                return failing.contains(order) ? new ShipmentOutcome.Failed("jammed")
                        : new ShipmentOutcome.Shipped("TRK-" + order.number());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ShipmentOutcome.Failed("interrupted");
            } catch (BrokenBarrierException | TimeoutException e) {
                return new ShipmentOutcome.Failed("barrier: " + e);
            } finally {
                inFlight.decrementAndGet();
            }
        }
    }

    private final Clock clock = Clock.fixed(Instant.parse("2026-11-16T06:00:00Z"), ZoneOffset.UTC);
    private final InMemoryOrderRepository orders = new InMemoryOrderRepository();
    private final List<ShopEvent> events = new ArrayList<>();
    private final List<Thread> eventThreads = new ArrayList<>();
    private final EventDispatcher dispatcher = new EventDispatcher(e -> {
        throw new AssertionError(e);
    });
    private final CountingWarehouse warehouse = new CountingWarehouse();

    FulfilmentServiceTest() {
        dispatcher.subscribe(ShopEvent.class, event -> {
            events.add(event);
            eventThreads.add(Thread.currentThread());
        });
    }

    private FulfilmentService service(int maxParallel) {
        return new FulfilmentService(orders, warehouse, clock, new UnitOfWork(dispatcher), maxParallel);
    }

    private OrderId paidOrder(long number, ProductType type) {
        Order placed = Order.builder().id(OrderId.of(number)).customer(new CustomerId("alice"))
                .item(new OrderItem(new Sku("BOK-001"), "Book", type, 1, Money.of("10.00")))
                .total(Money.of("10.00")).shipTo(new Address("A", "B", "C", "34710")).placedAt(clock.instant()).build();
        orders.save(placed.after((Transition.Allowed) OrderLifecycle.pay(placed.state(), "txn"), clock.instant()));
        return placed.id();
    }

    @Test
    void runsOrdersInParallelOnVirtualThreadsButNeverAboveTheLimit() {
        for (int i = 1; i <= 6; i++) {
            paidOrder(i, ProductType.PHYSICAL);
        }
        warehouse.barrier = new CyclicBarrier(2); // only passable when two orders are in the warehouse at once

        FulfilmentReport report = service(2).fulfilPaidOrders();

        assertThat(report.failed()).isEmpty();
        assertThat(report.shipped()).hasSize(6);
        assertThat(warehouse.peak.get()).isEqualTo(2);
        assertThat(warehouse.virtualThreads).hasSize(6).containsOnly(true);
    }

    @Test
    void failuresStayPaidAndEventsComeInOrderNumberOrderOnTheCallingThread() {
        OrderId first = paidOrder(1, ProductType.PHYSICAL);
        OrderId second = paidOrder(2, ProductType.PHYSICAL);
        OrderId third = paidOrder(10, ProductType.DIGITAL);
        warehouse.failing.add(second);

        FulfilmentReport report = service(4).fulfilPaidOrders();

        assertThat(report).isEqualTo(new FulfilmentReport(List.of(first, third),
                List.of(new FulfilmentFailure(second, "jammed"))));
        assertThat(orders.find(second).orElseThrow().status()).isEqualTo(OrderStatus.PAID);
        assertThat(events).containsExactly(new OrderShipped(first, "TRK-1"), new OrderShipped(third, "DIGITAL"));
        assertThat(eventThreads).containsOnly(Thread.currentThread());
        assertThat(warehouse.virtualThreads).as("the digital order never reached the warehouse").hasSize(2);
    }
}
