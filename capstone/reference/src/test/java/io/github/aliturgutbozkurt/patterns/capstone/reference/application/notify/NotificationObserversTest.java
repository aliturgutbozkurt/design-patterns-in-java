package io.github.aliturgutbozkurt.patterns.capstone.reference.application.notify;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderCancelled;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderPaid;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderShipped;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.StockLow;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryOrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.EventDispatcher;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Order;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderItem;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotificationObserversTest {

    private final List<String> sent = new ArrayList<>();
    private final EventDispatcher dispatcher = new EventDispatcher(e -> {
        throw new AssertionError(e);
    });
    private final InMemoryOrderRepository orders = new InMemoryOrderRepository();

    NotificationObserversTest() {
        orders.save(Order.builder().id(OrderId.of(1)).customer(new CustomerId("alice"))
                .item(new OrderItem(new Sku("BOK-001"), "Book", ProductType.PHYSICAL, 1, Money.of("250.00")))
                .total(Money.of("274.90")).shipTo(new Address("A", "B", "C", "D"))
                .placedAt(Instant.parse("2026-11-16T06:00:00Z")).build());
        new CustomerNotifier(orders, (to, subject, body) -> sent.add(to + " | " + subject + " | " + body))
                .subscribeTo(dispatcher);
        new StockAlerts((to, subject, body) -> sent.add(to + " | " + subject + " | " + body)).subscribeTo(dispatcher);
    }

    @Test
    void customerHearsAboutConfirmationShipmentAndCancellation() {
        dispatcher.dispatchAll(List.of(new OrderPaid(OrderId.of(1), "txn-1"),
                new OrderShipped(OrderId.of(1), "TRK-0001"), new OrderCancelled(OrderId.of(1), "late", false)));

        assertThat(sent).containsExactly(
                "alice | Order order-1 confirmed | Thank you! We received 274.90 for order-1.",
                "alice | Order order-1 shipped | Your order is on its way. Tracking code: TRK-0001.",
                "alice | Order order-1 cancelled | Your order was cancelled: late.");
    }

    @Test
    void opsHearsAboutLowStock() {
        dispatcher.dispatchAll(List.of(new StockLow(new Sku("TOY-001"), 4)));

        assertThat(sent).containsExactly("ops | Stock low: TOY-001 | TOY-001 has 4 left.");
    }
}
