package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.PlaceOrderService;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderCommand;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderCommand.LineRequest;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderResult.Placed;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderResult.Rejected;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.OrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderEvent;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Sku;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/** The service alone, with hand-written doubles for all five ports — no adapter on the test path. */
class PlaceOrderServiceTest {

    private final List<String> log = new ArrayList<>();                    // shared by the spies: call order
    private final Map<Sku, Money> prices = Map.of(new Sku("BOOK-1"), Money.of("20.00"));
    private boolean approve = true;
    private boolean failSave;
    private final AtomicInteger ids = new AtomicInteger();

    /** Spy repository that can be told to fail, like a full disk. */
    private final OrderRepository orders = new OrderRepository() {
        @Override
        public void save(Order order) {
            if (failSave) {
                throw new IllegalStateException("disk full");
            }
            log.add("save " + order.id());
        }

        @Override
        public Optional<Order> findById(OrderId id) {
            return Optional.empty();
        }

        @Override
        public int count() {
            return 0;
        }
    };

    private final PlaceOrderService service = new PlaceOrderService(
            sku -> Optional.ofNullable(prices.get(sku)),                                    // stub catalogue
            (customer, amount) -> log.add("charge " + customer + " " + amount) && approve,  // spy payment
            orders,
            (OrderEvent event) -> log.add("publish " + event.getClass().getSimpleName()),   // spy publisher
            () -> new OrderId("order-" + ids.incrementAndGet()));                           // sequence

    private static PlaceOrderCommand command(String customer, LineRequest... lines) {
        return new PlaceOrderCommand(customer, List.of(lines));
    }

    @Test
    void chargesThenSavesThenPublishes() {
        var result = service.place(command("alice", new LineRequest(new Sku("BOOK-1"), 2)));
        assertThat(result).isEqualTo(new Placed(new OrderId("order-1"), Money.of("40.00")));
        assertThat(log).containsExactly("charge alice 40.00", "save order-1", "publish OrderPlaced");
    }

    @Test
    void declinedPaymentStopsBeforeTheRepository() {
        approve = false;
        assertThat(service.place(command("alice", new LineRequest(new Sku("BOOK-1"), 1))))
                .isEqualTo(new Rejected("payment declined"));
        assertThat(log).containsExactly("charge alice 20.00");
        assertThat(ids.get()).isZero();
    }

    @Test
    void failedSavePublishesNothingAndPropagates() {
        failSave = true;
        assertThatThrownBy(() -> service.place(command("alice", new LineRequest(new Sku("BOOK-1"), 1))))
                .isInstanceOf(IllegalStateException.class).hasMessage("disk full");
        assertThat(log).containsExactly("charge alice 20.00");
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> service.place(null));
        assertThatNullPointerException().isThrownBy(() -> new PlaceOrderService(null, null, null, null, null));
    }
}
