package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.Objects;

/**
 * The lifecycle state of an order. Each state is a record carrying only the data that exists in it (a placed order
 * has no payment reference, only shipped and delivered orders have a tracking code), and the transitions in
 * {@link OrderLifecycle} are exhaustive switches over these records.
 *
 * @see "capstone guide, Pattern map — State"
 */
@PatternRole(value = DesignPattern.STATE, role = "state (sealed interface of records)")
public sealed interface OrderState {

    /** The payment reference of a free order. */
    String FREE = "FREE";

    /** Created by checkout, not paid yet (a transient state: checkout pays at once). */
    record Placed() implements OrderState {
    }

    /**
     * Paid at checkout.
     *
     * @param paymentReference the provider's reference or {@link #FREE}
     */
    record Paid(String paymentReference) implements OrderState {
        public Paid {
            Objects.requireNonNull(paymentReference, "paymentReference");
        }
    }

    /**
     * Handed to the carrier.
     *
     * @param paymentReference the payment reference
     * @param trackingCode     the carrier's tracking code or {@code DIGITAL}
     */
    record Shipped(String paymentReference, String trackingCode) implements OrderState {
        public Shipped {
            Objects.requireNonNull(paymentReference, "paymentReference");
            Objects.requireNonNull(trackingCode, "trackingCode");
        }
    }

    /**
     * Received by the customer.
     *
     * @param paymentReference the payment reference
     * @param trackingCode     the tracking code
     */
    record Delivered(String paymentReference, String trackingCode) implements OrderState {
        public Delivered {
            Objects.requireNonNull(paymentReference, "paymentReference");
            Objects.requireNonNull(trackingCode, "trackingCode");
        }
    }

    /**
     * Cancelled by the customer while paid.
     *
     * @param paymentReference the refunded payment's reference
     * @param reason           the customer's reason
     */
    record Cancelled(String paymentReference, String reason) implements OrderState {
        public Cancelled {
            Objects.requireNonNull(paymentReference, "paymentReference");
            Objects.requireNonNull(reason, "reason");
        }

        /** Whether money was refunded (not for a free order). */
        public boolean refunded() {
            return !FREE.equals(paymentReference);
        }
    }

    /** The GIVEN status of this state. */
    default OrderStatus status() {
        return switch (this) {
            case Placed _ -> OrderStatus.PLACED;
            case Paid _ -> OrderStatus.PAID;
            case Shipped _ -> OrderStatus.SHIPPED;
            case Delivered _ -> OrderStatus.DELIVERED;
            case Cancelled _ -> OrderStatus.CANCELLED;
        };
    }

    /** The payment reference, {@code ""} before payment. */
    default String paymentReference() {
        return switch (this) {
            case Placed _ -> "";
            case Paid(var reference) -> reference;
            case Shipped(var reference, _) -> reference;
            case Delivered(var reference, _) -> reference;
            case Cancelled(var reference, _) -> reference;
        };
    }

    /** The tracking code, {@code ""} until shipped. */
    default String trackingCode() {
        return switch (this) {
            case Placed _, Paid _, Cancelled _ -> "";
            case Shipped(_, var tracking) -> tracking;
            case Delivered(_, var tracking) -> tracking;
        };
    }
}
