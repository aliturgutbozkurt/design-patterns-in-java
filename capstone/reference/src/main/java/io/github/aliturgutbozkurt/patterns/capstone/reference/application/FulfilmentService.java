package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderShipped;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentFailure;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentReport;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.Changes;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.Committed;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.UnitOfWork;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.OrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.ShipmentOutcome;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.Warehouse;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Order;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderItem;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderLifecycle;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Transition;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * Feature F9: ships every paid order, one virtual thread per order (thread-per-task), with a {@link Semaphore} that
 * lets at most {@code maxParallelOrders} talk to the warehouse at once. Each order's outcome is committed on its own
 * thread; the {@code OrderShipped} events are collected and dispatched on the caller's thread after the run, in
 * order-number order. A task that crashes (a bug, not a warehouse failure) is reported after the other orders'
 * events were dispatched, so a committed shipment is never left untold. Thread safety: workers share no mutable state except through the unit of work's lock and the
 * thread-safe repositories; results are joined through the futures.
 *
 * @see "capstone guide §1 Pattern map — Thread-per-task"
 */
@PatternRole(value = DesignPattern.THREAD_PER_TASK, role = "one virtual thread per order, bounded by a semaphore")
public final class FulfilmentService implements FulfilmentUseCase {

    /** The tracking code of an order that has only digital lines. */
    public static final String DIGITAL = "DIGITAL";

    /** What happened to one order in a run. */
    private sealed interface Outcome {
        OrderId order();
    }

    private record ShippedOrder(OrderId order) implements Outcome {
    }

    private record FailedOrder(OrderId order, String reason) implements Outcome {
    }

    private final OrderRepository orders;
    private final Warehouse warehouse;
    private final Clock clock;
    private final UnitOfWork unitOfWork;
    private final int maxParallelOrders;

    public FulfilmentService(OrderRepository orders, Warehouse warehouse, Clock clock, UnitOfWork unitOfWork,
                             int maxParallelOrders) {
        this.orders = Objects.requireNonNull(orders, "orders");
        this.warehouse = Objects.requireNonNull(warehouse, "warehouse");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.unitOfWork = Objects.requireNonNull(unitOfWork, "unitOfWork");
        this.maxParallelOrders = maxParallelOrders;
    }

    @Override
    public FulfilmentReport fulfilPaidOrders() {
        List<Order> paid = orders.findAll().stream().filter(order -> order.state() instanceof OrderState.Paid)
                .toList(); // in order-number order
        Semaphore permits = new Semaphore(maxParallelOrders);
        List<Committed<Outcome>> results = new ArrayList<>();
        RuntimeException crash = null;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Committed<Outcome>>> futures = paid.stream()
                    .map(order -> executor.submit(() -> fulfil(order, permits))).toList();
            for (Future<Committed<Outcome>> future : futures) {
                try {
                    results.add(join(future));
                } catch (RuntimeException e) { // keep joining: the other orders' shipments are already committed
                    if (crash == null) {
                        crash = e;
                    } else {
                        crash.addSuppressed(e);
                    }
                }
            }
        }
        results.forEach(result -> unitOfWork.dispatch(result.events())); // caller thread, order-number order
        if (crash != null) {
            throw crash; // after the committed shipments were dispatched, so no observer misses one
        }
        return report(results.stream().map(Committed::result).toList());
    }

    private Committed<Outcome> fulfil(Order order, Semaphore permits) throws InterruptedException {
        permits.acquire();
        try {
            List<OrderItem> physical = order.items().stream().filter(OrderItem::isPhysical).toList();
            ShipmentOutcome shipment = physical.isEmpty() ? new ShipmentOutcome.Shipped(DIGITAL)
                    : warehouse.ship(order.id(), physical, order.shippingAddress().postalCode());
            return switch (shipment) {
                case ShipmentOutcome.Shipped(var tracking) -> unitOfWork.runDeferred(changes -> commit(order.id(),
                        tracking, changes));
                case ShipmentOutcome.Failed(var reason) -> new Committed<>(new FailedOrder(order.id(), reason),
                        List.of());
            };
        } finally {
            permits.release();
        }
    }

    /** Records the shipment, unless the order changed meanwhile (e.g. it was cancelled). */
    private Outcome commit(OrderId id, String tracking, Changes changes) {
        Order current = orders.find(id).orElseThrow();
        return switch (OrderLifecycle.ship(current.state(), tracking)) {
            case Transition.Allowed allowed -> {
                orders.save(current.after(allowed, clock.instant()));
                changes.raise(new OrderShipped(id, tracking));
                yield new ShippedOrder(id);
            }
            case Transition.Refused(var reason) -> new FailedOrder(id, reason);
        };
    }

    private static Committed<Outcome> join(Future<Committed<Outcome>> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while waiting for fulfilment", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("fulfilment task failed", e.getCause());
        }
    }

    private static FulfilmentReport report(List<Outcome> outcomes) {
        List<OrderId> shipped = new ArrayList<>();
        List<FulfilmentFailure> failed = new ArrayList<>();
        for (Outcome outcome : outcomes) {
            switch (outcome) {
                case ShippedOrder(var order) -> shipped.add(order);
                case FailedOrder(var order, var reason) -> failed.add(new FulfilmentFailure(order, reason));
            }
        }
        return new FulfilmentReport(shipped, failed);
    }
}
