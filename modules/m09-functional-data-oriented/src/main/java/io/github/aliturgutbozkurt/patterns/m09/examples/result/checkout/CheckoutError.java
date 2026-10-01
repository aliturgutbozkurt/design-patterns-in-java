package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import java.util.Objects;

/**
 * Every way a checkout can fail, as data. Because the type is sealed, a caller's {@code switch} must handle all four.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public sealed interface CheckoutError permits CheckoutError.EmptyCart, CheckoutError.OutOfStock,
        CheckoutError.InvalidCoupon, CheckoutError.PaymentDeclined {

    record EmptyCart() implements CheckoutError {}

    record OutOfStock(String sku, int requested, int available) implements CheckoutError {
        public OutOfStock {
            Objects.requireNonNull(sku, "sku");
        }
    }

    record InvalidCoupon(String code) implements CheckoutError {
        public InvalidCoupon {
            Objects.requireNonNull(code, "code");
        }
    }

    record PaymentDeclined(long amountCents) implements CheckoutError {}
}
