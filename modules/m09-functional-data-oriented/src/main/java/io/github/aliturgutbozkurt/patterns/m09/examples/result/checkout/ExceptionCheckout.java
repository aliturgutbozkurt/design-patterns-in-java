package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import java.util.Objects;
import java.util.Optional;

/**
 * Checkout, exception style: the happy path reads top to bottom, but every failure leaves through a hidden exit
 * that the signature {@code Receipt checkout(CheckoutRequest)} does not mention.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public final class ExceptionCheckout {

    private final InMemoryInventory inventory;
    private final ScriptedPaymentGateway gateway;

    public ExceptionCheckout(InMemoryInventory inventory, ScriptedPaymentGateway gateway) {
        this.inventory = Objects.requireNonNull(inventory, "inventory");
        this.gateway = Objects.requireNonNull(gateway, "gateway");
    }

    /** @throws CheckoutException for an empty cart, a bad coupon, short stock or a declined payment */
    public Receipt checkout(CheckoutRequest request) {
        if (request.items().isEmpty()) {
            throw new CheckoutException.EmptyCart();
        }
        long total = request.totalCents();
        if (!request.coupon().isBlank()) {
            int percent = Coupons.percentOff(request.coupon())
                    .orElseThrow(() -> new CheckoutException.InvalidCoupon(request.coupon()));
            total = Coupons.discounted(total, percent);
        }
        for (CartItem item : request.items()) {
            int available = inventory.available(item.sku());
            if (available < item.quantity()) {
                throw new CheckoutException.OutOfStock(item.sku(), item.quantity(), available);
            }
        }
        request.items().forEach(inventory::reserve);
        Optional<String> paymentId = gateway.charge(request.card(), total);
        if (paymentId.isEmpty()) {
            request.items().forEach(inventory::release);
            throw new CheckoutException.PaymentDeclined(total);
        }
        return new Receipt(paymentId.get(), total);
    }
}
