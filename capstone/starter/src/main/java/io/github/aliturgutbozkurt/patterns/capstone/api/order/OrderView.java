package io.github.aliturgutbozkurt.patterns.capstone.api.order;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * GIVEN — do not modify. A snapshot of an order.
 *
 * @param id               the order
 * @param customer         who placed it
 * @param lines            the bought lines, in cart order
 * @param total            the amount charged at checkout
 * @param status           current status
 * @param paymentReference the provider's reference ({@code FREE} for a free order)
 * @param trackingCode     {@code ""} until shipped
 * @param placedAt         the clock's instant at checkout
 * @param history          every status change, oldest first; notes: the payment reference for {@code PAID}, the
 *                         tracking code for {@code SHIPPED}, the reason for {@code CANCELLED}, otherwise {@code ""}
 * @see "capstone brief, Business rules — Order lifecycle (F7)"
 */
public record OrderView(OrderId id, CustomerId customer, List<OrderLine> lines, Money total, OrderStatus status,
                        String paymentReference, String trackingCode, Instant placedAt, List<StatusChange> history) {

    public OrderView {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(customer, "customer");
        lines = List.copyOf(lines);
        Objects.requireNonNull(total, "total");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(paymentReference, "paymentReference");
        Objects.requireNonNull(trackingCode, "trackingCode");
        Objects.requireNonNull(placedAt, "placedAt");
        history = List.copyOf(history);
    }
}
