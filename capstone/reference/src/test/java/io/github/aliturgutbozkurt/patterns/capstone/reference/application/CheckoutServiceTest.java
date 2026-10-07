package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutResult;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryOrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.OrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Order;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CheckoutServiceTest {

    private static final Address HOME = new Address("Alice", "Street 1", "Istanbul", "34710");

    /** Repository double whose save fails like a full disk. */
    private static final class FailingOrderRepository implements OrderRepository {
        @Override
        public void save(Order order) {
            throw new IllegalStateException("disk full");
        }

        @Override
        public Optional<Order> find(OrderId id) {
            return Optional.empty();
        }

        @Override
        public List<Order> findAll() {
            return List.of();
        }
    }

    private final ApplicationFixture app = new ApplicationFixture();

    private CartId cartWithTwoToys() {
        CartId cart = app.cartService.open(new CustomerId("alice"));
        app.cartService.add(cart, new Sku("TOY-001"), 2);
        return cart;
    }

    @Test
    void failedCommitAfterTheChargeIsCompensatedByARefund() {
        CartId cart = cartWithTwoToys();

        assertThatIllegalStateException()
                .isThrownBy(() -> app.checkout(new FailingOrderRepository())
                        .checkout(new CheckoutRequest(cart, HOME, "tok")))
                .withMessage("disk full");

        assertThat(app.payments.calls).containsExactly("charge 289.90", "refund txn-1 289.90");
        assertThat(app.events).as("nothing was committed, so nothing is told").isEmpty();
        assertThat(app.cartService.view(cart).open()).isTrue();
    }

    @Test
    void successfulCheckoutChargesOnceAndDispatchesAfterCommit() {
        CartId cart = cartWithTwoToys();
        InMemoryOrderRepository orders = new InMemoryOrderRepository();

        CheckoutResult result = app.checkout(orders).checkout(new CheckoutRequest(cart, HOME, "tok"));

        assertThat(result).isEqualTo(new CheckoutResult.Placed(OrderId.of(1), Money.of("289.90"), "txn-1"));
        assertThat(app.payments.calls).containsExactly("charge 289.90");
        assertThat(app.events).hasSize(3); // placed, paid, stock low (6 → 4)
        assertThat(orders.find(OrderId.of(1))).isPresent();
    }
}
