package io.github.aliturgutbozkurt.patterns.m11.exercises.ex02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderEvent.OrderCancelled;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderEvent.OrderPaid;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderEvent.OrderPlaced;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderEvent.OrderShipped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Assignment 02 — order lifecycle with domain events dispatched after commit. One test per acceptance criterion. */
public abstract class Ex02Contract {

    /** Your {@code OrderLifecycleService}. */
    protected abstract OrderLifecycle newLifecycle(OrderStore store, Supplier<OrderId> ids,
                                                   Consumer<RuntimeException> errorHandler);

    private static final OrderId ORDER_1 = new OrderId("order-1");

    /** One log shared by the fake store ("commit …") and the handlers, so tests can see the order of things. */
    private final List<String> log = new ArrayList<>();
    private final List<RuntimeException> errors = new ArrayList<>();

    /** Fake store: in memory, can be switched to fail the next commit. */
    private final class FakeStore implements OrderStore {
        private final Map<OrderId, OrderSnapshot> rows = new HashMap<>();
        private boolean failNext;

        @Override
        public Optional<OrderSnapshot> load(OrderId id) {
            return Optional.ofNullable(rows.get(id));
        }

        @Override
        public void commit(OrderSnapshot snapshot) {
            if (failNext) {
                failNext = false;
                throw new IllegalStateException("store unavailable");
            }
            rows.put(snapshot.id(), snapshot);
            log.add("commit " + snapshot.id() + " " + snapshot.status());
        }
    }

    private final FakeStore store = new FakeStore();
    private int lastId;
    private OrderLifecycle orders;

    @BeforeEach
    void wire() {
        orders = newLifecycle(store, () -> new OrderId("order-" + ++lastId), errors::add);
    }

    private void logAll() {
        orders.subscribe(OrderEvent.class, event -> log.add(event.getClass().getSimpleName() + " " + event.id()));
    }

    @Test
    void placeCommitsThenDispatchesOrderPlaced() {
        List<OrderPlaced> placed = new ArrayList<>();
        orders.subscribe(OrderPlaced.class, placed::add);
        logAll();
        assertThat(orders.place(4700)).isEqualTo(ORDER_1);
        assertThat(placed).containsExactly(new OrderPlaced(ORDER_1, 4700));
        assertThat(log).containsExactly("commit order-1 PLACED", "OrderPlaced order-1");
        assertThat(orders.status(ORDER_1)).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    void eventsAreDispatchedOnlyAfterCommit() {
        List<OrderStatus> seenByHandler = new ArrayList<>();
        orders.subscribe(OrderPaid.class, paid -> seenByHandler.add(store.load(paid.id()).orElseThrow().status()));
        orders.place(4700);
        orders.pay(ORDER_1);
        assertThat(seenByHandler).containsExactly(OrderStatus.PAID); // the store already had the new state
    }

    @Test
    void failedCommitDispatchesNothingAndKeepsState() {
        orders.place(4700);
        logAll();
        log.clear();
        store.failNext = true;
        assertThatIllegalStateException().isThrownBy(() -> orders.pay(ORDER_1)).withMessage("store unavailable");
        assertThat(log).isEmpty();
        assertThat(orders.status(ORDER_1)).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    void payThenShipFollowsLifecycle() {
        logAll();
        orders.place(4700);
        orders.pay(ORDER_1);
        orders.ship(ORDER_1);
        assertThat(orders.status(ORDER_1)).isEqualTo(OrderStatus.SHIPPED);
        assertThat(log).containsExactly("commit order-1 PLACED", "OrderPlaced order-1", "commit order-1 PAID",
                "OrderPaid order-1", "commit order-1 SHIPPED", "OrderShipped order-1");
    }

    @Test
    void cannotPayTwice() {
        orders.place(4700);
        orders.pay(ORDER_1);
        assertThatIllegalStateException().isThrownBy(() -> orders.pay(ORDER_1));
        assertThat(orders.status(ORDER_1)).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void cannotShipUnpaidOrder() {
        orders.place(4700);
        assertThatIllegalStateException().isThrownBy(() -> orders.ship(ORDER_1));
        assertThat(orders.status(ORDER_1)).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    void cannotCancelShippedOrder() {
        orders.place(4700);
        orders.pay(ORDER_1);
        orders.ship(ORDER_1);
        assertThatIllegalStateException().isThrownBy(() -> orders.cancel(ORDER_1, "too late"));
        assertThat(orders.status(ORDER_1)).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void illegalTransitionCommitsAndDispatchesNothing() {
        orders.place(4700);
        orders.cancel(ORDER_1, "changed my mind");
        logAll();
        log.clear();
        assertThatIllegalStateException().isThrownBy(() -> orders.pay(ORDER_1));
        assertThatIllegalStateException().isThrownBy(() -> orders.ship(ORDER_1));
        assertThatIllegalStateException().isThrownBy(() -> orders.cancel(ORDER_1, "again"));
        assertThat(log).isEmpty();
        assertThat(orders.status(ORDER_1)).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelCarriesReason() {
        List<OrderCancelled> cancelled = new ArrayList<>();
        orders.subscribe(OrderCancelled.class, cancelled::add);
        orders.place(4700);
        orders.cancel(ORDER_1, "out of stock");
        orders.place(1200);
        orders.pay(new OrderId("order-2"));
        orders.cancel(new OrderId("order-2"), "fraud check");
        assertThat(cancelled).containsExactly(new OrderCancelled(ORDER_1, "out of stock"),
                new OrderCancelled(new OrderId("order-2"), "fraud check"));
    }

    @Test
    void unknownOrderIsRejected() {
        OrderId unknown = new OrderId("order-9");
        assertThatThrownBy(() -> orders.pay(unknown)).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> orders.ship(unknown)).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> orders.cancel(unknown, "x")).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> orders.status(unknown)).isInstanceOf(NoSuchElementException.class);
        assertThat(log).isEmpty();
    }

    @Test
    void rejectsNonPositiveTotal() {
        logAll();
        assertThatIllegalArgumentException().isThrownBy(() -> orders.place(0));
        assertThatIllegalArgumentException().isThrownBy(() -> orders.place(-1));
        assertThat(log).isEmpty();
    }

    @Test
    void typedSubscriberReceivesOnlyItsEventType() {
        List<OrderShipped> shipped = new ArrayList<>();
        orders.subscribe(OrderShipped.class, shipped::add);
        orders.place(4700);
        orders.pay(ORDER_1);
        orders.ship(ORDER_1);
        assertThat(shipped).containsExactly(new OrderShipped(ORDER_1));
    }

    @Test
    void supertypeSubscriberReceivesAllEvents() {
        List<OrderEvent> all = new ArrayList<>();
        orders.subscribe(OrderEvent.class, all::add);
        orders.place(4700);
        orders.pay(ORDER_1);
        orders.ship(ORDER_1);
        assertThat(all).containsExactly(new OrderPlaced(ORDER_1, 4700), new OrderPaid(ORDER_1),
                new OrderShipped(ORDER_1));
    }

    @Test
    void handlersRunInSubscriptionOrder() {
        orders.subscribe(OrderEvent.class, event -> log.add("first"));
        orders.subscribe(OrderPlaced.class, event -> log.add("second"));
        orders.subscribe(OrderEvent.class, event -> log.add("third"));
        orders.place(4700);
        assertThat(log).containsExactly("commit order-1 PLACED", "first", "second", "third");
    }

    @Test
    void failingHandlerDoesNotStopOthersOrUndoCommit() {
        orders.subscribe(OrderPlaced.class, event -> {
            throw new IllegalStateException("mail server down");
        });
        logAll();
        orders.place(4700);
        assertThat(errors).extracting(Throwable::getMessage).containsExactly("mail server down");
        assertThat(log).containsExactly("commit order-1 PLACED", "OrderPlaced order-1");
        assertThat(orders.status(ORDER_1)).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    void commandFromHandlerIsDispatchedAfterCurrentEvent() {
        orders.subscribe(OrderPaid.class, paid -> {
            log.add("warehouse ships " + paid.id());
            orders.ship(paid.id()); // executed (committed) now, its event waits
            log.add("warehouse done");
        });
        logAll();
        orders.place(4700);
        log.clear();
        orders.pay(ORDER_1);
        assertThat(log).containsExactly("commit order-1 PAID", "warehouse ships order-1", "commit order-1 SHIPPED",
                "warehouse done", "OrderPaid order-1", "OrderShipped order-1");
    }

    @Test
    void closedSubscriptionReceivesNothing() {
        List<OrderEvent> received = new ArrayList<>();
        Subscription subscription = orders.subscribe(OrderEvent.class, received::add);
        orders.place(4700);
        subscription.close();
        subscription.close();
        orders.pay(ORDER_1);
        assertThat(received).containsExactly(new OrderPlaced(ORDER_1, 4700));
    }

    @Test
    void eachEventIsDispatchedOnce() {
        List<OrderEvent> all = new ArrayList<>();
        orders.subscribe(OrderEvent.class, all::add);
        orders.place(4700);
        orders.place(1200);
        orders.pay(ORDER_1);
        orders.cancel(new OrderId("order-2"), "no stock");
        orders.ship(ORDER_1);
        assertThat(all).containsExactly(new OrderPlaced(ORDER_1, 4700), new OrderPlaced(new OrderId("order-2"), 1200),
                new OrderPaid(ORDER_1), new OrderCancelled(new OrderId("order-2"), "no stock"),
                new OrderShipped(ORDER_1));
    }

    @Test
    void rejectsNullArguments() {
        orders.place(4700);
        assertThatNullPointerException().isThrownBy(() -> orders.pay(null));
        assertThatNullPointerException().isThrownBy(() -> orders.cancel(ORDER_1, null));
        assertThatNullPointerException().isThrownBy(() -> orders.subscribe(null, event -> {}));
        assertThatNullPointerException().isThrownBy(() -> orders.subscribe(OrderPaid.class, null));
        assertThatNullPointerException().isThrownBy(() -> newLifecycle(null, () -> ORDER_1, errors::add));
        assertThatNullPointerException().isThrownBy(() -> newLifecycle(store, () -> ORDER_1, null));
        assertThat(orders.status(ORDER_1)).isEqualTo(OrderStatus.PLACED);
    }
}
