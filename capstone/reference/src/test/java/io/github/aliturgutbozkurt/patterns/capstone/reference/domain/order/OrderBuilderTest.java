package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class OrderBuilderTest {

    private static final OrderItem BOOK = new OrderItem(new Sku("BOK-001"), "Book", ProductType.PHYSICAL, 2,
            Money.of("250.00"));
    private static final Instant NOW = Instant.parse("2026-11-16T06:00:00Z");

    private static Order.Builder complete() {
        return Order.builder().id(OrderId.of(1)).customer(new CustomerId("alice")).item(BOOK)
                .total(Money.of("500.00")).shipTo(new Address("A", "B", "C", "D")).placedAt(NOW);
    }

    @Test
    void buildsAnOrderFromItsParts() {
        Order order = complete().build();

        assertThat(order.id()).isEqualTo(OrderId.of(1));
        assertThat(order.items()).containsExactly(BOOK);
        assertThat(order.placedAt()).isEqualTo(NOW);
    }

    @Test
    void refusesHalfBuiltOrders() {
        assertThatNullPointerException().isThrownBy(() -> complete().total(null).build()).withMessage("total");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Order.builder().id(OrderId.of(1)).customer(new CustomerId("alice"))
                        .total(Money.ZERO).shipTo(new Address("", "", "", "")).placedAt(NOW).build())
                .withMessage("an order needs at least one line");
    }
}
