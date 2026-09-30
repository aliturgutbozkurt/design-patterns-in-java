package io.github.aliturgutbozkurt.patterns.m08.examples.state;

import static io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.OrderStatus.CANCELLED;
import static io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.OrderStatus.DELIVERED;
import static io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.OrderStatus.NEW;
import static io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.OrderStatus.PAID;
import static io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.OrderStatus.SHIPPED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.IllegalTransitionException;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.Order;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.Order.Transition;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.OrderStatus;
import io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm.StateDiagram;
import io.github.aliturgutbozkurt.patterns.m08.support.Console;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EnumOrderTest {

    /** The expected table, written independently of the production {@code switch}. */
    private static final Map<OrderStatus, Set<OrderStatus>> EXPECTED = Map.of(
            NEW, Set.of(PAID, CANCELLED),
            PAID, Set.of(SHIPPED, CANCELLED),
            SHIPPED, Set.of(DELIVERED),
            DELIVERED, Set.of(),
            CANCELLED, Set.of());

    static Stream<Arguments> allPairs() {
        return Arrays.stream(OrderStatus.values())
                .flatMap(from -> Arrays.stream(OrderStatus.values()).map(to -> Arguments.of(from, to)));
    }

    @Test
    void thereAreExactly25Pairs() {
        assertThat(allPairs()).hasSize(25);
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("allPairs")
    void everyPairMatchesTheExpectedTable(OrderStatus from, OrderStatus to) {
        assertThat(from.canMoveTo(to)).isEqualTo(EXPECTED.get(from).contains(to));
    }

    @Test
    void invalidMoveThrowsAndLeavesStatusAndHistoryUnchanged() {
        var order = new Order("O-1");
        order.moveTo(PAID);
        order.moveTo(SHIPPED);
        assertThatThrownBy(() -> order.moveTo(PAID))
                .isInstanceOf(IllegalTransitionException.class)
                .hasMessage("cannot move order O-1 from SHIPPED to PAID");
        assertThat(order.status()).isEqualTo(SHIPPED);
        assertThat(order.history()).containsExactly(new Transition(NEW, PAID), new Transition(PAID, SHIPPED));
    }

    @Test
    void exceptionCarriesTheRejectedTransition() {
        var order = new Order("O-2");
        var error = catchThrowableOfType(IllegalTransitionException.class, () -> order.moveTo(DELIVERED));
        assertThat(error.orderId()).isEqualTo("O-2");
        assertThat(error.from()).isEqualTo(NEW);
        assertThat(error.to()).isEqualTo(DELIVERED);
    }

    @Test
    void deliveredAndCancelledAreTerminal() {
        assertThat(DELIVERED.next()).isEmpty();
        assertThat(CANCELLED.next()).isEmpty();
        assertThat(Arrays.stream(OrderStatus.values()).filter(OrderStatus::isTerminal))
                .containsExactly(DELIVERED, CANCELLED);
    }

    @Test
    void nextIsUnmodifiable() {
        assertThatThrownBy(() -> NEW.next().add(DELIVERED)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void historyListsTransitionsInOrderAndIsUnmodifiable() {
        var order = new Order("O-3");
        order.moveTo(PAID);
        order.moveTo(CANCELLED);
        assertThat(order.history()).containsExactly(new Transition(NEW, PAID), new Transition(PAID, CANCELLED));
        assertThat(order.history()).isUnmodifiable();
        assertThat(order.history().getFirst()).hasToString("NEW -> PAID");
    }

    @Test
    void mermaidIsGeneratedFromTheTransitionTable() {
        assertThat(StateDiagram.mermaid()).isEqualTo("""
                stateDiagram-v2
                    [*] --> NEW
                    NEW --> PAID
                    NEW --> CANCELLED
                    PAID --> SHIPPED
                    PAID --> CANCELLED
                    SHIPPED --> DELIVERED
                    DELIVERED --> [*]
                    CANCELLED --> [*]
                """);
    }

    @Test
    void demoPrintsTheTableTheDiagramAndARejectedMove() {
        assertThat(Console.capture(() -> EnumOrderDemo.main(new String[0]))).isEqualTo("""
                NEW        -> [PAID, CANCELLED]
                PAID       -> [SHIPPED, CANCELLED]
                SHIPPED    -> [DELIVERED]
                DELIVERED  -> [] (terminal)
                CANCELLED  -> [] (terminal)
                O-1: NEW -> PAID -> SHIPPED
                rejected: cannot move order O-1 from SHIPPED to PAID
                still SHIPPED, history [NEW -> PAID, PAID -> SHIPPED]
                O-1: SHIPPED -> DELIVERED (terminal: true)
                stateDiagram-v2
                    [*] --> NEW
                    NEW --> PAID
                    NEW --> CANCELLED
                    PAID --> SHIPPED
                    PAID --> CANCELLED
                    SHIPPED --> DELIVERED
                    DELIVERED --> [*]
                    CANCELLED --> [*]
                """);
    }
}
