package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.ids;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class SequentialIdsTest {

    @Test
    void namedFactoriesStartEachSequenceAtOne() {
        SequentialIds<CartId> carts = SequentialIds.forCarts();
        SequentialIds<OrderId> orders = SequentialIds.forOrders();

        assertThat(List.of(carts.next(), carts.next())).containsExactly(new CartId("cart-1"), new CartId("cart-2"));
        assertThat(orders.next()).isEqualTo(OrderId.of(1));
    }

    @Test
    void concurrentCallersNeverGetTheSameId() {
        SequentialIds<OrderId> orders = SequentialIds.forOrders();
        Set<OrderId> seen = ConcurrentHashMap.newKeySet();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 1_000).forEach(_ -> executor.submit(() -> seen.add(orders.next())));
        }

        assertThat(seen).hasSize(1_000).contains(OrderId.of(1), OrderId.of(1_000));
    }
}
