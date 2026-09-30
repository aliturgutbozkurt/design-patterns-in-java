package io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout;

import io.github.aliturgutbozkurt.patterns.m05.examples.facade.checkout.Rejected.Reason;
import java.util.Objects;
import java.util.Optional;

/**
 * Facade: one call for reserve → charge → ship, including the <em>compensation</em> when a later step fails
 * (refund the charge, release the stock). Callers never have to remember the undo order.
 *
 * @see "m05 lesson, section Facade — modern Java 27"
 */
public final class CheckoutFacade {

    private final Inventory inventory;
    private final PaymentGateway payments;
    private final ShippingService shipping;

    public CheckoutFacade(Inventory inventory, PaymentGateway payments, ShippingService shipping) {
        this.inventory = Objects.requireNonNull(inventory, "inventory");
        this.payments = Objects.requireNonNull(payments, "payments");
        this.shipping = Objects.requireNonNull(shipping, "shipping");
    }

    public CheckoutResult placeOrder(Cart cart, Card card, Address address) {
        Optional<String> reservation = inventory.reserve(cart.quantities());
        if (reservation.isEmpty()) {
            return new Rejected(Reason.OUT_OF_STOCK, "not enough stock");
        }
        String reservationId = reservation.orElseThrow();

        long total = cart.totalCents();
        Optional<String> payment = payments.charge(card, total);
        if (payment.isEmpty()) {
            inventory.release(reservationId);                        // undo step 1
            return new Rejected(Reason.PAYMENT_DECLINED, "card ending " + card.lastFour() + " declined");
        }
        String paymentId = payment.orElseThrow();

        Optional<String> tracking = shipping.ship(address, reservationId);
        if (tracking.isEmpty()) {
            payments.refund(paymentId);                              // undo step 2
            inventory.release(reservationId);                        // undo step 1
            return new Rejected(Reason.SHIPPING_UNAVAILABLE, "no shipping to " + address.country());
        }
        return new Placed(tracking.orElseThrow(), total);
    }
}
