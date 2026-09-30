package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.EmptyCart;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.InvalidCoupon;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.OutOfStock;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout.CheckoutError.PaymentDeclined;
import java.util.Locale;

/**
 * Text for the customer: one exhaustive {@code switch}, no {@code default}. A fifth error kind would not compile
 * until it gets a message here.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public final class CheckoutErrors {

    private CheckoutErrors() {}

    public static String message(CheckoutError error) {
        return switch (error) {
            case EmptyCart _ -> "your cart is empty";
            case OutOfStock(var sku, var requested, var available) ->
                    "only " + available + " x " + sku + " left (you asked for " + requested + ")";
            case InvalidCoupon(var code) -> "coupon " + code + " is not valid";
            case PaymentDeclined(var amountCents) -> "payment of " + money(amountCents) + " was declined";
        };
    }

    static String money(long cents) {
        return String.format(Locale.ROOT, "%d.%02d", cents / 100, cents % 100);
    }
}
