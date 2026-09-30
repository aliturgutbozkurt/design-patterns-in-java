package io.github.aliturgutbozkurt.patterns.m11.examples.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.DomainEventDispatcher;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderEvent;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderEvent.OrderCancelled;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderEvent.OrderPaid;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderEvent.OrderPlaced;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderEvent.OrderShipped;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderStatus;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderStore;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.UnitOfWork;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DomainEventsTest {

    private final List<String> log = new ArrayList<>();
    private final List<RuntimeException> errors = new ArrayList<>();
    private final DomainEventDispatcher<OrderEvent> dispatcher = new DomainEventDispatcher<>(errors::add);
    private final OrderStore store = new OrderStore();
    private final UnitOfWork unitOfWork = new UnitOfWork(store, dispatcher);

    private void logEverything() {
        dispatcher.subscribe(OrderEvent.class, event -> log.add(event.getClass().getSimpleName() + " " + event.orderId()));
    }

    @Test
    void noHandlerRunsBeforeCommit() {
        logEverything();
        Order order = Order.place("order-1", 4700);
        order.pay();
        unitOfWork.register(order);
        assertThat(log).isEmpty();
        unitOfWork.commit();
        assertThat(log).containsExactly("OrderPlaced order-1", "OrderPaid order-1");
    }

    @Test
    void afterCommitEventsAreDispatchedInTheOrderTheyWereRaised() {
        logEverything();
        Order first = Order.place("order-1", 4700);
        Order second = Order.place("order-2", 1200);
        first.pay();
        first.ship();
        second.cancel("out of stock");
        unitOfWork.register(first);
        unitOfWork.register(second);
        unitOfWork.commit();
        assertThat(log).containsExactly("OrderPlaced order-1", "OrderPaid order-1", "OrderShipped order-1",
                "OrderPlaced order-2", "OrderCancelled order-2");
        assertThat(store.statuses()).containsExactly(
                Map.entry("order-1", OrderStatus.SHIPPED), Map.entry("order-2", OrderStatus.CANCELLED));
    }

    @Test
    void failingStoreMeansCommitThrowsNoEventIsDispatchedAndTheStoreIsUnchanged() {
        logEverything();
        unitOfWork.register(Order.place("order-1", 4700));
        unitOfWork.commit();
        log.clear();

        Order paid = store.load("order-1").orElseThrow();
        paid.pay();
        unitOfWork.register(paid);
        unitOfWork.register(Order.place("order-2", 1200));
        store.failNextSave();

        assertThatIllegalStateException().isThrownBy(unitOfWork::commit).withMessage("store unavailable");
        assertThat(log).isEmpty();
        assertThat(store.statuses()).containsExactly(Map.entry("order-1", OrderStatus.PLACED));
    }

    @Test
    void rollbackDiscardsPendingEvents() {
        logEverything();
        Order order = Order.place("order-1", 4700);
        unitOfWork.register(order);
        unitOfWork.rollback();
        unitOfWork.commit();
        assertThat(log).isEmpty();
        assertThat(order.pullEvents()).isEmpty();
        assertThat(store.statuses()).isEmpty();
    }

    @Test
    void illegalTransitionThrowsAndRecordsNoEvent() {
        Order order = Order.place("order-1", 4700);
        order.cancel("changed my mind");
        order.pullEvents();
        assertThatIllegalStateException().isThrownBy(order::pay).withMessage("cannot pay order-1: it is CANCELLED");
        assertThatIllegalStateException().isThrownBy(order::ship).withMessage("cannot ship order-1: it is CANCELLED");
        assertThat(order.pullEvents()).isEmpty();
        assertThat(order.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void failingHandlerDoesNotUndoTheCommitAndOtherHandlersStillRun() {
        dispatcher.subscribe(OrderPlaced.class, placed -> {
            throw new IllegalStateException("mail server down");
        });
        logEverything();
        unitOfWork.register(Order.place("order-1", 4700));
        unitOfWork.commit();
        assertThat(errors).extracting(Throwable::getMessage).containsExactly("mail server down");
        assertThat(log).containsExactly("OrderPlaced order-1");
        assertThat(store.statuses()).containsExactly(Map.entry("order-1", OrderStatus.PLACED));
    }

    @Test
    void eventRaisedByAHandlerIsDispatchedAfterTheCurrentEventsHandlersFinish() {
        // Paying ships the order at once: the handler runs a second unit of work from inside the first dispatch.
        dispatcher.subscribe(OrderPaid.class, paid -> {
            log.add("warehouse ships " + paid.orderId());
            Order order = store.load(paid.orderId()).orElseThrow();
            order.ship();
            unitOfWork.register(order);
            unitOfWork.commit();
            log.add("warehouse done");
        });
        logEverything();
        Order order = Order.place("order-1", 4700);
        order.pay();
        unitOfWork.register(order);
        unitOfWork.commit();
        assertThat(log).containsExactly("OrderPlaced order-1", "warehouse ships order-1", "warehouse done",
                "OrderPaid order-1", "OrderShipped order-1");
    }

    @Test
    void pullEventsEmptiesTheAggregatesListSoNoEventIsDispatchedTwice() {
        logEverything();
        Order order = Order.place("order-1", 4700);
        unitOfWork.register(order);
        unitOfWork.commit();
        unitOfWork.register(order);
        unitOfWork.commit();
        assertThat(log).containsExactly("OrderPlaced order-1");
    }

    @Test
    void typedSubscriberReceivesOnlyItsEventType() {
        List<OrderCancelled> cancellations = new ArrayList<>();
        dispatcher.subscribe(OrderCancelled.class, cancellations::add);
        Order order = Order.place("order-1", 4700);
        order.cancel("too slow");
        unitOfWork.register(order);
        unitOfWork.commit();
        assertThat(cancellations).containsExactly(new OrderCancelled("order-1", "too slow"));
    }

    @Test
    void invalidInputIsRejected() {
        assertThatIllegalArgumentException().isThrownBy(() -> Order.place("order-1", 0));
        assertThatNullPointerException().isThrownBy(() -> unitOfWork.register(null));
        assertThatNullPointerException().isThrownBy(() -> dispatcher.subscribe(OrderShipped.class, null));
        assertThatNullPointerException().isThrownBy(() -> dispatcher.dispatch(null));
    }

    @Test
    void demoPrintsCommitThenDispatch() {
        assertThat(Console.capture(() -> DomainEventsDemo.main(new String[0]))).isEqualTo("""
                recorded, not committed yet: nothing dispatched
                mail: order order-1 confirmed, total 47.00
                audit: OrderPlaced[orderId=order-1, totalCents=4700]
                stock: reserved for order-1
                audit: OrderPaid[orderId=order-1]
                stored: {order-1=PAID}
                commit failed (store unavailable): stored {order-1=PAID}, nothing dispatched
                illegal transition: cannot pay order-1: it is CANCELLED
                audit: OrderCancelled[orderId=order-1, reason=customer changed their mind]
                stored: {order-1=CANCELLED}
                """);
    }
}
