package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.events;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.EventPublisher;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Outbound adapter that keeps every published event; a real system would put a bus or an outbox here.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public final class RecordingEventPublisher implements EventPublisher {

    private final List<OrderEvent> published = new ArrayList<>();

    @Override
    public void publish(OrderEvent event) {
        published.add(Objects.requireNonNull(event, "event"));
    }

    /** Every event, oldest first (an unmodifiable copy). */
    public List<OrderEvent> published() {
        return List.copyOf(published);
    }
}
