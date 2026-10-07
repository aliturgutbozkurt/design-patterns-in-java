package io.github.aliturgutbozkurt.patterns.capstone.reference.application.events;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderDelivered;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderPaid;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.Subscription;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class EventDispatcherTest {

    private final List<Throwable> errors = new ArrayList<>();
    private final EventDispatcher dispatcher = new EventDispatcher(errors::add);
    private final ShopEvent paid = new OrderPaid(OrderId.of(1), "txn-1");
    private final ShopEvent delivered = new OrderDelivered(OrderId.of(1));

    @Test
    void typedHandlersReceiveTheirTypeAndSupertypeHandlersEverything() {
        List<OrderPaid> payments = new ArrayList<>();
        List<ShopEvent> all = new ArrayList<>();
        dispatcher.subscribe(OrderPaid.class, payments::add);
        dispatcher.subscribe(ShopEvent.class, all::add);

        dispatcher.dispatchAll(List.of(paid, delivered));

        assertThat(payments).containsExactly((OrderPaid) paid);
        assertThat(all).containsExactly(paid, delivered);
    }

    @Test
    void eventsDispatchedByAHandlerAreQueuedAfterTheCurrentOne() {
        List<String> log = new ArrayList<>();
        dispatcher.subscribe(OrderPaid.class, _ -> {
            log.add("paid-1");
            dispatcher.dispatchAll(List.of(delivered));
        });
        dispatcher.subscribe(ShopEvent.class, event -> log.add("all " + event.getClass().getSimpleName()));

        dispatcher.dispatchAll(List.of(paid));

        assertThat(log).containsExactly("paid-1", "all OrderPaid", "all OrderDelivered");
    }

    @Test
    void failingHandlerIsReportedAndTheOthersStillRun() {
        IllegalStateException bug = new IllegalStateException("bug");
        List<ShopEvent> received = new ArrayList<>();
        dispatcher.subscribe(ShopEvent.class, _ -> {
            throw bug;
        });
        dispatcher.subscribe(ShopEvent.class, received::add);

        dispatcher.dispatchAll(List.of(paid, delivered));

        assertThat(errors).containsExactly(bug, bug);
        assertThat(received).containsExactly(paid, delivered);
    }

    @Test
    void closedSubscriptionReceivesNothingAndClosingTwiceIsHarmless() {
        List<ShopEvent> received = new ArrayList<>();
        Subscription subscription = dispatcher.subscribe(ShopEvent.class, received::add);
        dispatcher.subscribe(ShopEvent.class, received::add);

        subscription.close();
        subscription.close();
        dispatcher.dispatchAll(List.of(paid));

        assertThat(received).containsExactly(paid);
    }
}
