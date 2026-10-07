package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutResult;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryOrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.OrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.ProductRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.PhysicalProduct;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.Product;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.Specification;
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
        public void remove(OrderId id) {
            // nothing was saved, so there is nothing to remove
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

    /** Product repository double that reads through to the fixture's products and fails on its second save. */
    private static final class SecondSaveFails implements ProductRepository {
        private final ProductRepository products;
        private int saves;

        SecondSaveFails(ProductRepository products) {
            this.products = products;
        }

        @Override
        public void save(Product product) {
            if (++saves == 2) {
                throw new IllegalStateException("disk full");
            }
            products.save(product);
        }

        @Override
        public Optional<Product> find(Sku sku) {
            return products.find(sku);
        }

        @Override
        public List<Product> findMatching(Specification<Product> specification) {
            return products.findMatching(specification);
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
    void failureHalfwayThroughTheCommitUndoesEveryWriteAndRefunds() {
        app.products.save(new PhysicalProduct(new Sku("HOM-001"), "Mug", Category.HOME, Money.of("10.00"), 50));
        CartId cart = cartWithTwoToys();
        app.cartService.add(cart, new Sku("HOM-001"), 1);
        InMemoryOrderRepository orders = new InMemoryOrderRepository();
        Inventory failingStock = new Inventory(new SecondSaveFails(app.products), 5);

        assertThatIllegalStateException()
                .isThrownBy(() -> app.checkout(orders, failingStock).checkout(new CheckoutRequest(cart, HOME, "tok")))
                .withMessage("disk full");

        assertThat(app.payments.calls).containsExactly("charge 299.90", "refund txn-1 299.90");
        assertThat(orders.findAll()).as("no order is left behind").isEmpty();
        assertThat(app.cartService.view(cart).open()).as("the cart is open again").isTrue();
        assertThat(app.inventory.stockOf(new Sku("TOY-001"))).as("the first reservation is undone").isEqualTo(6);
        assertThat(app.inventory.stockOf(new Sku("HOM-001"))).isEqualTo(50);
        assertThat(app.events).isEmpty();
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
