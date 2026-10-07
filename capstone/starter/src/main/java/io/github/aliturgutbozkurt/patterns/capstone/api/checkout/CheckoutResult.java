package io.github.aliturgutbozkurt.patterns.capstone.api.checkout;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import java.util.List;
import java.util.Objects;

/**
 * GIVEN — do not modify. The business outcome of a checkout.
 *
 * @see "capstone brief §2.2 — Checkout (F5, F6)"
 */
public sealed interface CheckoutResult {

    /**
     * The order was placed and paid.
     *
     * @param order            the new order's id
     * @param total            the amount charged
     * @param paymentReference the provider's reference, or {@code FREE} for a total of 0.00
     */
    record Placed(OrderId order, Money total, String paymentReference) implements CheckoutResult {
        public Placed {
            Objects.requireNonNull(order, "order");
            Objects.requireNonNull(total, "total");
            Objects.requireNonNull(paymentReference, "paymentReference");
        }
    }

    /**
     * Nothing happened: no order, no stock change, the cart stays open, no event, no notification.
     *
     * @param reasons at least one reason, in rule order
     */
    record Rejected(List<String> reasons) implements CheckoutResult {
        public Rejected {
            reasons = List.copyOf(reasons);
            if (reasons.isEmpty()) {
                throw new IllegalArgumentException("a rejection needs a reason");
            }
        }
    }
}
