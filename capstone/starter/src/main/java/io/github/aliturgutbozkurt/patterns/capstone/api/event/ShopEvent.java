package io.github.aliturgutbozkurt.patterns.capstone.api.event;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * GIVEN — do not modify. The domain events of PatternShop.
 *
 * @see "capstone brief, Business rules — Events and notifications (F8)"
 */
public sealed interface ShopEvent {

    /**
     * A checkout created the order (raised before {@link OrderPaid}).
     *
     * @param order    the order
     * @param customer who placed it
     * @param total    the amount charged
     */
    record OrderPlaced(OrderId order, CustomerId customer, Money total) implements ShopEvent {
        public OrderPlaced {
            Objects.requireNonNull(order, "order");
            Objects.requireNonNull(customer, "customer");
            Objects.requireNonNull(total, "total");
        }
    }

    /**
     * The order was paid at checkout.
     *
     * @param order            the order
     * @param paymentReference the provider's reference or {@code FREE}
     */
    record OrderPaid(OrderId order, String paymentReference) implements ShopEvent {
        public OrderPaid {
            Objects.requireNonNull(order, "order");
            Objects.requireNonNull(paymentReference, "paymentReference");
        }
    }

    /**
     * Fulfilment shipped the order.
     *
     * @param order        the order
     * @param trackingCode the warehouse's tracking code, or {@code DIGITAL}
     */
    record OrderShipped(OrderId order, String trackingCode) implements ShopEvent {
        public OrderShipped {
            Objects.requireNonNull(order, "order");
            Objects.requireNonNull(trackingCode, "trackingCode");
        }
    }

    /**
     * The order was delivered.
     *
     * @param order the order
     */
    record OrderDelivered(OrderId order) implements ShopEvent {
        public OrderDelivered {
            Objects.requireNonNull(order, "order");
        }
    }

    /**
     * The order was cancelled.
     *
     * @param order    the order
     * @param reason   the customer's reason
     * @param refunded whether a payment was refunded ({@code false} for a free order)
     */
    record OrderCancelled(OrderId order, String reason, boolean refunded) implements ShopEvent {
        public OrderCancelled {
            Objects.requireNonNull(order, "order");
            Objects.requireNonNull(reason, "reason");
        }
    }

    /**
     * A physical product's stock dropped from at least {@code lowStockThreshold} to below it.
     *
     * @param sku       the product
     * @param remaining units left
     */
    record StockLow(Sku sku, int remaining) implements ShopEvent {
        public StockLow {
            Objects.requireNonNull(sku, "sku");
        }
    }
}
