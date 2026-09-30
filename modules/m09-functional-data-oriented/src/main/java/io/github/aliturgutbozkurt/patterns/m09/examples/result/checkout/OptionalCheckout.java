package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import java.util.Objects;
import java.util.Optional;

/**
 * Checkout, {@code Optional} style: failure is visible in the signature, but every failure looks the same. The
 * caller learns <em>that</em> it failed, never <em>why</em>.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public final class OptionalCheckout {

    private final InMemoryInventory inventory;
    private final ScriptedPaymentGateway gateway;

    public OptionalCheckout(InMemoryInventory inventory, ScriptedPaymentGateway gateway) {
        this.inventory = Objects.requireNonNull(inventory, "inventory");
        this.gateway = Objects.requireNonNull(gateway, "gateway");
    }

    public Optional<Receipt> checkout(CheckoutRequest request) {
        return Optional.of(request)
                .filter(r -> !r.items().isEmpty())
                .flatMap(OptionalCheckout::applyCoupon)
                .filter(this::inStock)
                .flatMap(this::pay);
    }

    private static Optional<Priced> applyCoupon(CheckoutRequest request) {
        if (request.coupon().isBlank()) {
            return Optional.of(new Priced(request, request.totalCents()));
        }
        return Coupons.percentOff(request.coupon())
                .map(percent -> new Priced(request, Coupons.discounted(request.totalCents(), percent)));
    }

    private boolean inStock(Priced priced) {
        return priced.request().items().stream().allMatch(i -> inventory.available(i.sku()) >= i.quantity());
    }

    private Optional<Receipt> pay(Priced priced) {
        var items = priced.request().items();
        items.forEach(inventory::reserve);
        Optional<Receipt> receipt = gateway.charge(priced.request().card(), priced.totalCents())
                .map(paymentId -> new Receipt(paymentId, priced.totalCents()));
        if (receipt.isEmpty()) {
            items.forEach(inventory::release);
        }
        return receipt;
    }
}
