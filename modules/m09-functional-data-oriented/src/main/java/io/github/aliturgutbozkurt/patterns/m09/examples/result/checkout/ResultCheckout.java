package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.EmptyCart;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.InvalidCoupon;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.OutOfStock;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.PaymentDeclined;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Err;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Ok;
import java.util.Objects;

/**
 * Checkout, {@code Result} style ("railway"): each step is a {@code flatMap}; the first {@code Err} switches to the
 * failure track and no later step runs. The signature names the error type, and the caller must handle every kind.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public final class ResultCheckout {

    private final InMemoryInventory inventory;
    private final ScriptedPaymentGateway gateway;

    public ResultCheckout(InMemoryInventory inventory, ScriptedPaymentGateway gateway) {
        this.inventory = Objects.requireNonNull(inventory, "inventory");
        this.gateway = Objects.requireNonNull(gateway, "gateway");
    }

    public Result<Receipt, CheckoutError> checkout(CheckoutRequest request) {
        return nonEmpty(request)
                .flatMap(ResultCheckout::applyCoupon)
                .flatMap(this::checkStock)
                .flatMap(this::pay);
    }

    private static Result<CheckoutRequest, CheckoutError> nonEmpty(CheckoutRequest request) {
        return request.items().isEmpty() ? Result.err(new EmptyCart()) : Result.ok(request);
    }

    private static Result<Priced, CheckoutError> applyCoupon(CheckoutRequest request) {
        if (request.coupon().isBlank()) {
            return Result.ok(new Priced(request, request.totalCents()));
        }
        return Coupons.percentOff(request.coupon())
                .<Result<Priced, CheckoutError>>map(percent ->
                        Result.ok(new Priced(request, Coupons.discounted(request.totalCents(), percent))))
                .orElseGet(() -> Result.err(new InvalidCoupon(request.coupon())));
    }

    private Result<Priced, CheckoutError> checkStock(Priced priced) {
        for (CartItem item : priced.request().items()) {
            int available = inventory.available(item.sku());
            if (available < item.quantity()) {
                return Result.err(new OutOfStock(item.sku(), item.quantity(), available));
            }
        }
        return Result.ok(priced);
    }

    private Result<Receipt, CheckoutError> pay(Priced priced) {
        var items = priced.request().items();
        items.forEach(inventory::reserve);
        Result<Receipt, CheckoutError> charged = gateway.charge(priced.request().card(), priced.totalCents())
                .<Result<Receipt, CheckoutError>>map(paymentId -> Result.ok(new Receipt(paymentId, priced.totalCents())))
                .orElseGet(() -> Result.err(new PaymentDeclined(priced.totalCents())));
        return switch (charged) {
            case Ok<Receipt, CheckoutError> ok -> ok;
            case Err<Receipt, CheckoutError> declined -> {
                items.forEach(inventory::release);
                yield declined;
            }
        };
    }
}
