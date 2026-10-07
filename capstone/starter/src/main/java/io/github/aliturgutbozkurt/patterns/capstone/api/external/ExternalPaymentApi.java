package io.github.aliturgutbozkurt.patterns.capstone.api.external;

/**
 * GIVEN — do not modify. A third-party payment provider with an awkward, stringly-typed interface. Your core must not
 * depend on it: reach it only through an outbound adapter.
 *
 * <p>Status codes: 200 approved, 402 declined, 5xx provider unavailable. Amounts are decimal strings with two fraction
 * digits ({@code "987.91"}), the currency is {@code "TRY"}.
 *
 * @see "capstone brief §2.2 — Checkout (F5, F6)"
 */
public interface ExternalPaymentApi {

    /** Charges a card; {@code idempotencyKey} identifies the attempt (PatternShop uses the cart id). */
    GatewayResponse authorize(String merchantId, String cardToken, String amount, String currency,
                              String idempotencyKey);

    /** Refunds an approved payment by its {@code reference}; 200 means refunded. */
    GatewayResponse refund(String merchantId, String reference, String amount, String currency);
}
