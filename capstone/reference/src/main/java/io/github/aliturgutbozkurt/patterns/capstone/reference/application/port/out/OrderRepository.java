package io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Order;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port: where orders live. Implementations are thread-safe.
 *
 * @see "capstone guide §1 Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.REPOSITORY, role = "repository (outbound port)")
public interface OrderRepository {

    /** Inserts or replaces the order with the same id. */
    void save(Order order);

    /** The order with this id, if any. */
    Optional<Order> find(OrderId id);

    /** Every order, in order-number order (= placement order). */
    List<Order> findAll();
}
