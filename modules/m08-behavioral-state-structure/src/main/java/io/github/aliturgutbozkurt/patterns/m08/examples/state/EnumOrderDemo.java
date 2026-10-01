package io.github.aliturgutbozkurt.patterns.m08.examples.state;

import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.IllegalTransitionException;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.Order;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.OrderStatus;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.StateDiagram;
import java.util.Locale;

/**
 * Run: {@code java modules/m08-behavioral-state-structure/src/main/java/io/github/aliturgutbozkurt/patterns/m08/examples/state/EnumOrderDemo.java}
 *
 * @see "m08 lesson, section State"
 */
public final class EnumOrderDemo {

    private EnumOrderDemo() {}

    public static void main(String[] args) {
        for (OrderStatus status : OrderStatus.values()) {
            System.out.print(String.format(Locale.ROOT, "%-10s -> %s%s\n",
                    status, status.next(), status.isTerminal() ? " (terminal)" : ""));
        }

        var order = new Order("O-1");
        order.moveTo(OrderStatus.PAID);
        order.moveTo(OrderStatus.SHIPPED);
        System.out.println(order.id() + ": " + path(order));
        try {
            order.moveTo(OrderStatus.PAID);
        } catch (IllegalTransitionException e) {
            System.out.println("rejected: " + e.getMessage());
        }
        System.out.println("still " + order.status() + ", history " + order.history());
        order.moveTo(OrderStatus.DELIVERED);
        System.out.println(order.id() + ": " + order.history().getLast() + " (terminal: "
                + order.status().isTerminal() + ")");

        System.out.print(StateDiagram.mermaid());
    }

    private static String path(Order order) {
        var text = new StringBuilder(order.history().getFirst().from().name());
        order.history().forEach(step -> text.append(" -> ").append(step.to()));
        return text.toString();
    }
}
