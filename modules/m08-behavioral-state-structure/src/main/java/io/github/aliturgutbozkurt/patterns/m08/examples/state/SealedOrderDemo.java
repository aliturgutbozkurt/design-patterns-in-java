package io.github.aliturgutbozkurt.patterns.m08.examples.state;

import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.Line;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.AddLine;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.Cancel;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.Deliver;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.Pay;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.Place;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderEvent.Ship;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderMachine;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.Transition.Moved;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.Transition.Rejected;
import java.util.List;
import java.util.Locale;

/** Run: {@code java modules/m08-behavioral-state-structure/src/main/java/io/github/aliturgutbozkurt/patterns/m08/examples/state/SealedOrderDemo.java} */
public final class SealedOrderDemo {

    private SealedOrderDemo() {}

    public static void main(String[] args) {
        var book = new Line("BOOK", 2, 350);
        var mug = new Line("MUG", 1, 300);

        OrderState state = OrderMachine.initial();
        System.out.println(String.format(Locale.ROOT, "%-8s %s", "start", state));
        for (OrderEvent event : List.of(new AddLine(book), new AddLine(mug), new Place(), new Pay("PAY-7", 900),
                new Pay("PAY-7", 1000), new Cancel("customer request"))) {
            String label = String.format(Locale.ROOT, "%-8s ", event.getClass().getSimpleName());
            switch (OrderMachine.apply(state, event)) {
                case Moved(var next) -> {
                    state = next;
                    System.out.println(label + "-> " + next);
                }
                case Rejected(var reason) -> System.out.println(label + "rejected: " + reason);
            }
        }

        List<OrderEvent> delivered = List.of(new AddLine(book), new AddLine(mug), new Place(),
                new Pay("PAY-7", 1000), new Ship("TRK-42"), new Deliver());
        System.out.println("replay:  " + OrderMachine.replay(delivered));
        List<OrderEvent> lateCancel = List.of(new AddLine(book), new AddLine(mug), new Place(),
                new Pay("PAY-7", 1000), new Ship("TRK-42"), new Cancel("too late"), new Deliver());
        System.out.println("replay:  " + OrderMachine.replay(lateCancel));
    }
}
