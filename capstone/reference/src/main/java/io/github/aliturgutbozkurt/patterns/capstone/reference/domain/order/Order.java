package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The order aggregate: lines and total fixed at checkout, plus its lifecycle state and history. Created only through
 * {@link #builder()} (an order has many parts, several of them computed at checkout, and a half-built order must never
 * exist); changed only by applying an allowed {@link Transition}.
 *
 * @param id              the order
 * @param customer        who placed it
 * @param items           the bought lines, in cart order (at least one)
 * @param total           the amount charged
 * @param shippingAddress where physical items go
 * @param placedAt        the clock's instant at checkout
 * @param state           the current lifecycle state
 * @param history         every status change, oldest first
 * @see "capstone guide §1 Pattern map — Builder, State"
 */
@PatternRole(value = DesignPattern.BUILDER, role = "product")
@PatternRole(value = DesignPattern.STATE, role = "context")
public record Order(OrderId id, CustomerId customer, List<OrderItem> items, Money total, Address shippingAddress,
                    Instant placedAt, OrderState state, List<HistoryEntry> history) {

    public Order {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(customer, "customer");
        items = List.copyOf(items);
        Objects.requireNonNull(total, "total");
        Objects.requireNonNull(shippingAddress, "shippingAddress");
        Objects.requireNonNull(placedAt, "placedAt");
        Objects.requireNonNull(state, "state");
        history = List.copyOf(history);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("an order needs at least one line");
        }
    }

    /** A builder for a new order in state {@code PLACED}. */
    public static Builder builder() {
        return new Builder();
    }

    /** The current status. */
    public OrderStatus status() {
        return state.status();
    }

    /** This order after an allowed transition at {@code at}: new state, one more history entry. */
    public Order after(Transition.Allowed transition, Instant at) {
        List<HistoryEntry> longer = new ArrayList<>(history);
        longer.add(new HistoryEntry(transition.next().status(), at, transition.note()));
        return new Order(id, customer, items, total, shippingAddress, placedAt, transition.next(), longer);
    }

    /**
     * Collects the parts of an order step by step and validates them once, in {@link #build()}.
     *
     * @see "capstone guide §1 Pattern map — Builder"
     */
    @PatternRole(value = DesignPattern.BUILDER, role = "builder")
    public static final class Builder {

        private OrderId id;
        private CustomerId customer;
        private final List<OrderItem> items = new ArrayList<>();
        private Money total;
        private Address shippingAddress;
        private Instant placedAt;

        private Builder() {
        }

        public Builder id(OrderId id) {
            this.id = id;
            return this;
        }

        public Builder customer(CustomerId customer) {
            this.customer = customer;
            return this;
        }

        public Builder item(OrderItem item) {
            items.add(Objects.requireNonNull(item, "item"));
            return this;
        }

        public Builder total(Money total) {
            this.total = total;
            return this;
        }

        public Builder shipTo(Address address) {
            this.shippingAddress = address;
            return this;
        }

        public Builder placedAt(Instant instant) {
            this.placedAt = instant;
            return this;
        }

        /** The order in state {@code PLACED}; a missing part throws {@link NullPointerException} naming it. */
        public Order build() {
            Objects.requireNonNull(placedAt, "placedAt");
            return new Order(id, customer, items, total, shippingAddress, placedAt, new OrderState.Placed(),
                    List.of(new HistoryEntry(OrderStatus.PLACED, placedAt, "")));
        }
    }
}
