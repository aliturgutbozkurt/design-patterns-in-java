package io.github.aliturgutbozkurt.patterns.m09.examples.dop.order;

import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.classic.LegacyOrderService;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.classic.MutableOrder;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Draft;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Paid;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Placed;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.Order.Shipped;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.OrderId;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.OrderLine;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.OrderTransitions;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.order.modern.OrderViews;
import java.util.List;

/** Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/dop/order/OrderLifecycleDemo.java} */
public final class OrderLifecycleDemo {

    private OrderLifecycleDemo() {}

    public static void main(String[] args) {
        System.out.println("-- classic: a status string and nullable fields");
        var legacy = new LegacyOrderService();
        var noTracking = new MutableOrder("A-1", 45_00);
        noTracking.setStatus("SHIPPED");
        System.out.println(legacy.describe(noTracking));
        var typo = new MutableOrder("A-2", 10_00);
        typo.setStatus("SHIPED");
        System.out.println(legacy.describe(typo));
        var both = new MutableOrder("A-3", 10_00);
        legacy.place(both);
        legacy.pay(both, "PAY-1");
        legacy.ship(both, "TRK-1");
        both.setStatus("CANCELLED");
        both.setCancelReason("lost in transit");
        System.out.println(legacy.describe(both) + " -- but its tracking code is still " + both.getTrackingCode());

        System.out.println("-- modern: one record per state, transitions typed by their input");
        List<OrderLine> lines = List.of(new OrderLine("MUG-0001", 2, 12_50), new OrderLine("TEE-0002", 1, 20_00));
        Draft draft = new Draft(new OrderId("A-1"), lines);
        Placed placed = OrderTransitions.place(draft).orElseGet(error -> {
            throw new IllegalStateException("unexpected: " + error);
        });
        Paid paid = OrderTransitions.pay(placed, "PAY-7");
        Shipped shipped = OrderTransitions.ship(paid, "TRK-9");
        // OrderTransitions.ship(placed, "TRK-9");   <- does not compile: ship needs a Paid order
        for (Order order : List.<Order>of(draft, placed, paid, shipped)) {
            System.out.println(OrderViews.describe(order));
        }
        System.out.println("cancel A-1 (shipped) -> " + OrderTransitions.cancel(shipped, "too late"));
        var paidA4 = new Paid(new OrderId("A-4"), lines, "PAY-8");
        OrderTransitions.cancel(paidA4, "changed mind")
                .map(OrderViews::describe)
                .toOptional()
                .ifPresent(System.out::println);
        System.out.println("place A-5 (empty)    -> "
                + OrderTransitions.place(new Draft(new OrderId("A-5"), List.of())));
    }
}
