package io.github.aliturgutbozkurt.patterns.m08.examples.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

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
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Cancelled;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Delivered;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Draft;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Paid;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Placed;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.OrderState.Shipped;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.ReplayResult;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.Transition;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.Transition.Moved;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.sealed.Transition.Rejected;
import io.github.aliturgutbozkurt.patterns.m08.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SealedOrderTest {

    private static final Line BOOK = new Line("BOOK", 2, 350);
    private static final Line MUG = new Line("MUG", 1, 300);
    private static final List<OrderEvent> HAPPY_LOG = List.of(
            new AddLine(BOOK), new AddLine(MUG), new Place(), new Pay("PAY-7", 1000), new Ship("TRK-42"),
            new Deliver());

    private static OrderState moved(Transition transition) {
        assertThat(transition).isInstanceOf(Moved.class);
        return ((Moved) transition).state();
    }

    private static OrderState placed() {
        return new Placed(List.of(BOOK, MUG), 1000);
    }

    @Test
    void happyPathProducesTheExactStateRecords() {
        var states = new ArrayList<OrderState>();
        OrderState state = OrderMachine.initial();
        for (OrderEvent event : HAPPY_LOG) {
            state = moved(OrderMachine.apply(state, event));
            states.add(state);
        }
        assertThat(states).containsExactly(
                new Draft(List.of(BOOK)),
                new Draft(List.of(BOOK, MUG)),
                new Placed(List.of(BOOK, MUG), 1000),
                new Paid(1000, "PAY-7"),
                new Shipped("PAY-7", "TRK-42"),
                new Delivered("TRK-42"));
    }

    @Test
    void payingTheWrongAmountIsRejectedWithTheReason() {
        assertThat(OrderMachine.apply(placed(), new Pay("PAY-7", 900)))
                .isEqualTo(new Rejected("amount 900 does not match total 1000"));
    }

    @Test
    void placingAnEmptyDraftIsRejected() {
        assertThat(OrderMachine.apply(OrderMachine.initial(), new Place()))
                .isEqualTo(new Rejected("cannot place an empty order"));
    }

    @Test
    void cancelFromPlacedIsNotRefundedAndFromPaidIsRefunded() {
        assertThat(moved(OrderMachine.apply(placed(), new Cancel("changed my mind"))))
                .isEqualTo(new Cancelled("changed my mind", false));
        assertThat(moved(OrderMachine.apply(new Paid(1000, "PAY-7"), new Cancel("out of stock"))))
                .isEqualTo(new Cancelled("out of stock", true));
    }

    @Test
    void cancelFromShippedIsRejected() {
        assertThat(OrderMachine.apply(new Shipped("PAY-7", "TRK-42"), new Cancel("too late")))
                .isEqualTo(new Rejected("Cancel not allowed in Shipped"));
    }

    @Test
    void rejectedNeverChangesTheState() {
        OrderState before = placed();
        assertThat(OrderMachine.apply(before, new Ship("TRK-1"))).isInstanceOf(Rejected.class);
        assertThat(before).isEqualTo(placed());
        var stopped = (ReplayResult.Stopped) OrderMachine.replay(List.of(new AddLine(BOOK), new Deliver()));
        assertThat(stopped.state()).isEqualTo(new Draft(List.of(BOOK)));
    }

    @Test
    void replayOfAStoredLogEqualsStepByStepApplication() {
        OrderState stepByStep = OrderMachine.initial();
        for (OrderEvent event : HAPPY_LOG) {
            stepByStep = moved(OrderMachine.apply(stepByStep, event));
        }
        assertThat(OrderMachine.replay(HAPPY_LOG)).isEqualTo(new ReplayResult.Completed(stepByStep));
    }

    @Test
    void replayStopsAtTheFirstRejectionAndReportsItsIndex() {
        var log = List.of(new AddLine(BOOK), new AddLine(MUG), new Place(), new Pay("PAY-7", 900),
                new Pay("PAY-8", 1000));
        assertThat(OrderMachine.replay(log)).isEqualTo(new ReplayResult.Stopped(
                new Placed(List.of(BOOK, MUG), 1000), 3, "amount 900 does not match total 1000"));
    }

    @Test
    void linesAreCopiedSoCallersCannotChangeAState() {
        var lines = new ArrayList<>(List.of(BOOK));
        var draft = new Draft(lines);
        lines.add(MUG);
        assertThat(draft.lines()).containsExactly(BOOK).isUnmodifiable();
    }

    @Test
    void recordsValidateTheirData() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Line("BOOK", 0, 350));
        assertThatIllegalArgumentException().isThrownBy(() -> new Pay("PAY-1", 0));
        assertThatIllegalArgumentException().isThrownBy(() -> new Ship(" "));
    }

    @Test
    void demoPrintsEachEventWithTheResultingStateAndTheReplays() {
        assertThat(Console.capture(() -> SealedOrderDemo.main(new String[0]))).isEqualTo("""
                start    Draft[lines=[]]
                AddLine  -> Draft[lines=[2 x BOOK @ 350]]
                AddLine  -> Draft[lines=[2 x BOOK @ 350, 1 x MUG @ 300]]
                Place    -> Placed[lines=[2 x BOOK @ 350, 1 x MUG @ 300], totalCents=1000]
                Pay      rejected: amount 900 does not match total 1000
                Pay      -> Paid[totalCents=1000, paymentId=PAY-7]
                Cancel   -> Cancelled[reason=customer request, refunded=true]
                replay:  Completed[state=Delivered[trackingNo=TRK-42]]
                replay:  Stopped[state=Shipped[paymentId=PAY-7, trackingNo=TRK-42], index=5, reason=Cancel not allowed in Shipped]
                """);
    }
}
