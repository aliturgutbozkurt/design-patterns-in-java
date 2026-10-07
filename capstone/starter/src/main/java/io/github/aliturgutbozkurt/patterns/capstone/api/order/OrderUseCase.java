package io.github.aliturgutbozkurt.patterns.capstone.api.order;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import java.util.List;
import java.util.Optional;

/**
 * GIVEN — do not modify. Inbound port of feature F7: reading orders and the customer-initiated transitions. Business
 * outcomes are {@link TransitionResult}s, never exceptions.
 *
 * @see "capstone brief, Business rules — Order lifecycle (F7)"
 */
public interface OrderUseCase {

    /** The order with this id, if it exists. */
    Optional<OrderView> find(OrderId order);

    /** The customer's orders in placement order (empty if none). */
    List<OrderView> ordersOf(CustomerId customer);

    /**
     * {@code PAID → CANCELLED}: refunds through the payment provider (unless the order was free) and restocks physical
     * products. Refused with {@code unknown order: order-9}, {@code cannot cancel SHIPPED order},
     * {@code missing reason} (blank reason) or {@code refund failed} (then nothing changes), checked in this order.
     */
    TransitionResult cancel(OrderId order, String reason);

    /** {@code SHIPPED → DELIVERED}. Refused with {@code unknown order: order-9} or {@code cannot deliver PAID order}. */
    TransitionResult markDelivered(OrderId order);
}
