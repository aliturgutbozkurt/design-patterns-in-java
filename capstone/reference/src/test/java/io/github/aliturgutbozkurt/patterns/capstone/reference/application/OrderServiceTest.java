package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderCancelled;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.TransitionResult;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryOrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentOutcome;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

    private final ApplicationFixture app = new ApplicationFixture();
    private final InMemoryOrderRepository orders = new InMemoryOrderRepository();
    private final OrderService service = app.orderService(orders);

    private OrderId placeTwoToys() {
        CartId cart = app.cartService.open(new CustomerId("alice"));
        app.cartService.add(cart, new Sku("TOY-001"), 2);
        app.checkout(orders).checkout(new CheckoutRequest(cart, new Address("A", "B", "C", "D"), "tok"));
        return OrderId.of(1);
    }

    @Test
    void cancellationRefundsRestocksAndRaisesItsEvent() {
        OrderId id = placeTwoToys();

        assertThat(service.cancel(id, "changed my mind")).isInstanceOf(TransitionResult.Done.class);

        assertThat(app.payments.calls).containsExactly("charge 289.90", "refund txn-1 289.90");
        assertThat(app.inventory.stockOf(new Sku("TOY-001"))).isEqualTo(6);
        assertThat(app.events.getLast()).isEqualTo(new OrderCancelled(id, "changed my mind", true));
    }

    @Test
    void failedRefundChangesNothing() {
        OrderId id = placeTwoToys();
        app.payments.refundOutcome = new PaymentOutcome.Unavailable("500");
        int eventsBefore = app.events.size();

        assertThat(service.cancel(id, "changed my mind")).isEqualTo(new TransitionResult.Refused("refund failed"));

        assertThat(service.find(id).orElseThrow().status()).isEqualTo(OrderStatus.PAID);
        assertThat(app.inventory.stockOf(new Sku("TOY-001"))).isEqualTo(4);
        assertThat(app.events).hasSize(eventsBefore);
    }
}
