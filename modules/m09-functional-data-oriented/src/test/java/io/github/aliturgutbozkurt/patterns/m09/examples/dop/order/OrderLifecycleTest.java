package io.github.aliturgutbozkurt.patterns.m09.examples.dop.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Cancelled;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Draft;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Paid;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Placed;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Shipped;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.OrderId;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.OrderLine;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.OrderTransitions;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.OrderViews;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.TransitionError.AlreadyCancelled;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.TransitionError.AlreadyShipped;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.TransitionError.EmptyOrder;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Err;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Ok;
import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderLifecycleTest {

    private static final OrderId ID = new OrderId("A-1");
    private static final List<OrderLine> LINES =
            List.of(new OrderLine("MUG-0001", 2, 12_50), new OrderLine("TEE-0002", 1, 20_00));
    private static final Draft DRAFT = new Draft(ID, LINES);
    private static final Placed PLACED = new Placed(ID, LINES);
    private static final Paid PAID = new Paid(ID, LINES, "PAY-7");
    private static final Shipped SHIPPED = new Shipped(ID, LINES, "PAY-7", "TRK-9");

    @Test
    void compactConstructorsRejectBlankReferencesAndCopyTheLines() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Paid(ID, LINES, " "))
                .withMessage("paymentRef must not be blank");
        assertThatIllegalArgumentException().isThrownBy(() -> new Shipped(ID, LINES, "PAY-7", ""))
                .withMessage("trackingCode must not be blank");
        var source = new ArrayList<>(LINES);
        var draft = new Draft(ID, source);
        source.clear();
        assertThat(draft.lines()).hasSize(2).isUnmodifiable();
    }

    @Test
    void placedOrdersCannotBeEmpty() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Placed(ID, List.of()))
                .withMessage("a placed order needs at least one line");
    }

    @Test
    void placingAnEmptyDraftIsAnErr() {
        assertThat(OrderTransitions.place(new Draft(new OrderId("A-5"), List.of())))
                .isEqualTo(new Err<>(new EmptyOrder(new OrderId("A-5"))));
        assertThat(OrderTransitions.place(DRAFT)).isEqualTo(new Ok<>(PLACED));
    }

    @Test
    void payAndShipMoveTheOrderForward() {
        assertThat(OrderTransitions.pay(PLACED, "PAY-7")).isEqualTo(PAID);
        assertThat(OrderTransitions.ship(PAID, "TRK-9")).isEqualTo(SHIPPED);
    }

    @Test
    void shipAcceptsOnlyPaidOrders() {
        List<Method> ships = Arrays.stream(OrderTransitions.class.getDeclaredMethods())
                .filter(m -> m.getName().equals("ship"))
                .toList();
        assertThat(ships).singleElement()
                .satisfies(m -> assertThat(m.getParameterTypes()).containsExactly(Paid.class, String.class));
    }

    @Test
    void cancellingAShippedOrCancelledOrderIsAnErr() {
        assertThat(OrderTransitions.cancel(SHIPPED, "too late")).isEqualTo(new Err<>(new AlreadyShipped(ID)));
        var cancelled = new Cancelled(ID, LINES, "changed mind", false);
        assertThat(OrderTransitions.cancel(cancelled, "again")).isEqualTo(new Err<>(new AlreadyCancelled(ID)));
    }

    @Test
    void cancellingAPaidOrderMeansARefundIsDue() {
        assertThat(OrderTransitions.cancel(PAID, "changed mind"))
                .isEqualTo(new Ok<>(new Cancelled(ID, LINES, "changed mind", true)));
        assertThat(OrderTransitions.cancel(DRAFT, "changed mind"))
                .isEqualTo(new Ok<>(new Cancelled(ID, LINES, "changed mind", false)));
        assertThat(OrderTransitions.cancel(PLACED, "changed mind"))
                .isEqualTo(new Ok<>(new Cancelled(ID, LINES, "changed mind", false)));
    }

    @Test
    void describeGivesExactTextForEachState() {
        assertThat(OrderViews.describe(DRAFT)).isEqualTo("A-1: draft, 2 line(s), total 45.00");
        assertThat(OrderViews.describe(PLACED)).isEqualTo("A-1: placed, awaiting payment of 45.00");
        assertThat(OrderViews.describe(PAID)).isEqualTo("A-1: paid (PAY-7)");
        assertThat(OrderViews.describe(SHIPPED)).isEqualTo("A-1: shipped, tracking TRK-9");
        assertThat(OrderViews.describe(new Cancelled(ID, LINES, "changed mind", true)))
                .isEqualTo("A-1: cancelled (changed mind), refund due");
        assertThat(OrderViews.describe(new Cancelled(ID, LINES, "changed mind", false)))
                .isEqualTo("A-1: cancelled (changed mind), nothing to refund");
    }

    @Test
    void totalIsTheSameInEveryState() {
        List<Order> states = List.of(DRAFT, PLACED, PAID, SHIPPED, new Cancelled(ID, LINES, "x", true));
        assertThat(states).extracting(OrderViews::totalCents).containsOnly(45_00L);
    }

    @Test
    void orderPermitsExactlyTheFiveStateRecords() {
        assertThat(Order.class.getPermittedSubclasses())
                .containsExactly(Draft.class, Placed.class, Paid.class, Shipped.class, Cancelled.class);
        assertThat(Order.class.getPermittedSubclasses()).allMatch(Class::isRecord);
    }

    @Test
    void transitionsCompose() {
        Result<Shipped, ?> shipped = OrderTransitions.place(DRAFT)
                .map(placed -> OrderTransitions.pay(placed, "PAY-7"))
                .map(paid -> OrderTransitions.ship(paid, "TRK-9"));
        assertThat(shipped).isEqualTo(new Ok<>(SHIPPED));
    }

    @Test
    void demoPrintsTheClassicProblemsAndTheModernLifecycle() {
        assertThat(Console.capture(() -> OrderLifecycleDemo.main(new String[0]))).isEqualTo("""
                -- classic: a status string and nullable fields
                A-1: shipped, tracking null
                A-2: unknown
                A-3: cancelled (lost in transit) -- but its tracking code is still TRK-1
                -- modern: one record per state, transitions typed by their input
                A-1: draft, 2 line(s), total 45.00
                A-1: placed, awaiting payment of 45.00
                A-1: paid (PAY-7)
                A-1: shipped, tracking TRK-9
                cancel A-1 (shipped) -> Err[error=AlreadyShipped[id=A-1]]
                A-4: cancelled (changed mind), refund due
                place A-5 (empty)    -> Err[error=EmptyOrder[id=A-5]]
                """);
    }
}
