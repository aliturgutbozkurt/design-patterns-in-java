package io.github.aliturgutbozkurt.patterns.m11.examples.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.OrderStatus;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox.IdempotentConsumer;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox.OutboxEntry;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox.OutboxOrderStore;
import io.github.aliturgutbozkurt.patterns.m11.examples.events.outbox.OutboxRelay;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OutboxTest {

    private final OutboxOrderStore store = new OutboxOrderStore();
    private final OutboxRelay relay = new OutboxRelay(store);
    private final List<OutboxEntry> sent = new ArrayList<>();

    private void saveThreeEvents() {
        Order order = Order.place("order-1", 4700);
        order.pay();
        order.ship();
        store.save(order);
    }

    @Test
    void savingAnOrderStoresItsStateAndItsEventsTogether() {
        saveThreeEvents();
        assertThat(store.orders()).containsExactly(Map.entry("order-1", OrderStatus.SHIPPED));
        assertThat(store.outbox()).containsExactly(
                new OutboxEntry(1, "OrderPlaced", "order-1 4700", false),
                new OutboxEntry(2, "OrderPaid", "order-1", false),
                new OutboxEntry(3, "OrderShipped", "order-1", false));
    }

    @Test
    void failingWriteLeavesNeitherStateNorEvents() {
        saveThreeEvents();
        Order second = Order.place("order-2", 1200);
        store.failNextWrite();
        assertThatIllegalStateException().isThrownBy(() -> store.save(second)).withMessage("disk full");
        assertThat(store.orders()).containsOnlyKeys("order-1");
        assertThat(store.outbox()).hasSize(3);

        store.save(second); // retry: the order still has its event
        assertThat(store.outbox()).last().isEqualTo(new OutboxEntry(4, "OrderPlaced", "order-2 1200", false));
    }

    @Test
    void relayPublishesPendingEntriesInSequenceOrderAndMarksThem() {
        saveThreeEvents();
        assertThat(relay.relayPending(sent::add)).isEqualTo(3);
        assertThat(sent).extracting(OutboxEntry::sequence).containsExactly(1L, 2L, 3L);
        assertThat(store.pending()).isEmpty();
        assertThat(relay.relayPending(sent::add)).isZero();
    }

    @Test
    void brokerFailureLeavesThatEntryAndAllLaterOnesPendingAndTheNextRelayResumes() {
        saveThreeEvents();
        assertThatIllegalStateException().isThrownBy(() -> relay.relayPending(entry -> {
            if (entry.sequence() == 2) {
                throw new IllegalStateException("broker unavailable");
            }
            sent.add(entry);
        })).withMessage("broker unavailable");
        assertThat(store.pending()).extracting(OutboxEntry::sequence).containsExactly(2L, 3L);

        assertThat(relay.relayPending(sent::add)).isEqualTo(2);
        assertThat(sent).extracting(OutboxEntry::sequence).containsExactly(1L, 2L, 3L);
    }

    @Test
    void entryRedeliveredAfterACrashIsProcessedOnceByTheIdempotentConsumer() {
        saveThreeEvents();
        List<Long> processed = new ArrayList<>();
        var consumer = new IdempotentConsumer(entry -> processed.add(entry.sequence()));
        boolean[] crash = {true};
        assertThatIllegalStateException().isThrownBy(() -> relay.relayPending(entry -> {
            consumer.receive(entry);
            if (crash[0]) {
                crash[0] = false;
                throw new IllegalStateException("crash between publish and mark");
            }
        }));
        relay.relayPending(consumer::receive);
        assertThat(processed).containsExactly(1L, 2L, 3L);
        assertThat(consumer.duplicates()).isEqualTo(1);
    }

    @Test
    void demoPrintsAtLeastOnceDelivery() {
        assertThat(Console.capture(() -> OutboxDemo.main(new String[0]))).isEqualTo("""
                saved order-1 with its events: pending [1, 2]
                write failed (disk full): orders {order-1=PAID}, outbox entries 2
                relay failed (broker unavailable): pending [1, 2]
                relay failed (relay crashed before marking 1): pending [1, 2]
                relayed 2: pending []
                consumer processed: [1 OrderPlaced order-1 4700, 2 OrderPaid order-1], duplicates ignored: 1
                """);
    }
}
