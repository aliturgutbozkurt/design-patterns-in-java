package io.github.aliturgutbozkurt.patterns.capstone.reference.application.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderDelivered;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class UnitOfWorkTest {

    private final List<ShopEvent> received = new ArrayList<>();
    private final EventDispatcher dispatcher = new EventDispatcher(e -> {
        throw new AssertionError(e);
    });
    private final UnitOfWork unitOfWork = new UnitOfWork(dispatcher);
    private final ShopEvent event = new OrderDelivered(OrderId.of(1));

    UnitOfWorkTest() {
        dispatcher.subscribe(ShopEvent.class, received::add);
    }

    @Test
    void eventsAreDispatchedOnlyAfterTheWorkCompleted() {
        String result = unitOfWork.run(changes -> {
            changes.raise(event);
            assertThat(received).as("not yet: the change is not committed").isEmpty();
            return "done";
        });

        assertThat(result).isEqualTo("done");
        assertThat(received).containsExactly(event);
    }

    @Test
    void failedWorkDispatchesNothing() {
        assertThatIllegalStateException().isThrownBy(() -> unitOfWork.run(changes -> {
            changes.raise(event);
            throw new IllegalStateException("commit failed");
        }));

        assertThat(received).isEmpty();
    }

    @Test
    void nestedWorkJoinsTheOuterTransaction() {
        unitOfWork.run(outer -> {
            outer.raise(event);
            unitOfWork.run(inner -> {
                inner.raise(event);
                return null;
            });
            assertThat(received).as("the inner run does not dispatch").isEmpty();
            return null;
        });

        assertThat(received).containsExactly(event, event);
    }

    @Test
    void deferredRunHandsTheEventsBack() {
        Committed<Integer> committed = unitOfWork.runDeferred(changes -> {
            changes.raise(event);
            return 42;
        });

        assertThat(received).isEmpty();
        assertThat(committed).isEqualTo(new Committed<>(42, List.of(event)));
        unitOfWork.dispatch(committed.events());
        assertThat(received).containsExactly(event);
    }
}
