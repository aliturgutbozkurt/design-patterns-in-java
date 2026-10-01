package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

/**
 * The exception style: one subclass per failure. They are unchecked, so nothing in {@code checkout}'s signature tells
 * the caller that they exist.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public abstract sealed class CheckoutException extends RuntimeException permits CheckoutException.EmptyCart,
        CheckoutException.OutOfStock, CheckoutException.InvalidCoupon, CheckoutException.PaymentDeclined {

    // Exceptions are Serializable; -Xlint:all asks every serializable class for an explicit version id.
    private static final long serialVersionUID = 1L;

    private CheckoutException(String message) {
        super(message);
    }

    /** The cart has no items. */
    public static final class EmptyCart extends CheckoutException {
        private static final long serialVersionUID = 1L;

        public EmptyCart() {
            super(CheckoutErrors.message(new CheckoutError.EmptyCart()));
        }
    }

    /** Not enough stock for one SKU. */
    public static final class OutOfStock extends CheckoutException {
        private static final long serialVersionUID = 1L;

        public OutOfStock(String sku, int requested, int available) {
            super(CheckoutErrors.message(new CheckoutError.OutOfStock(sku, requested, available)));
        }
    }

    /** The coupon code is unknown. */
    public static final class InvalidCoupon extends CheckoutException {
        private static final long serialVersionUID = 1L;

        public InvalidCoupon(String code) {
            super(CheckoutErrors.message(new CheckoutError.InvalidCoupon(code)));
        }
    }

    /** The payment gateway said no. */
    public static final class PaymentDeclined extends CheckoutException {
        private static final long serialVersionUID = 1L;

        public PaymentDeclined(long amountCents) {
            super(CheckoutErrors.message(new CheckoutError.PaymentDeclined(amountCents)));
        }
    }
}
