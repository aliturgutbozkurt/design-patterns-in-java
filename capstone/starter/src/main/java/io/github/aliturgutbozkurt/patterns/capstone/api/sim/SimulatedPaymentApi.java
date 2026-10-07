package io.github.aliturgutbozkurt.patterns.capstone.api.sim;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.ExternalPaymentApi;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.GatewayResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GIVEN — do not modify. A deterministic, thread-safe stand-in for the payment provider: card token
 * {@value #APPROVED} → 200 with reference {@code txn-1}, {@code txn-2}, …; {@value #UNAVAILABLE} → 503; any other
 * token (e.g. {@value #DECLINED}) → 402. An approved idempotency key is answered with the same reference again.
 * Refunding an approved, not yet refunded reference → 200; anything else → 404.
 *
 * @see "capstone brief §2.2 — Checkout (F5, F6)"
 */
public final class SimulatedPaymentApi implements ExternalPaymentApi {

    /** Card token that is always approved. */
    public static final String APPROVED = "tok_visa_ok";
    /** Card token that is always declined. */
    public static final String DECLINED = "tok_declined";
    /** Card token for which the provider is down. */
    public static final String UNAVAILABLE = "tok_unavailable";

    private final AtomicInteger transactions = new AtomicInteger();
    private final AtomicInteger refunds = new AtomicInteger();
    private final Map<String, String> approvedByKey = new ConcurrentHashMap<>();
    private final Set<String> refundable = ConcurrentHashMap.newKeySet();

    @Override
    public GatewayResponse authorize(String merchantId, String cardToken, String amount, String currency,
                                     String idempotencyKey) {
        Objects.requireNonNull(merchantId, "merchantId");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey");
        return switch (Objects.requireNonNull(cardToken, "cardToken")) {
            case APPROVED -> {
                String reference = approvedByKey.computeIfAbsent(idempotencyKey,
                        _ -> "txn-" + transactions.incrementAndGet());
                refundable.add(reference);
                yield new GatewayResponse(200, reference, "approved");
            }
            case UNAVAILABLE -> new GatewayResponse(503, "", "service unavailable");
            default -> new GatewayResponse(402, "", "card declined");
        };
    }

    @Override
    public GatewayResponse refund(String merchantId, String reference, String amount, String currency) {
        Objects.requireNonNull(merchantId, "merchantId");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        if (refundable.remove(Objects.requireNonNull(reference, "reference"))) {
            return new GatewayResponse(200, "rfd-" + refunds.incrementAndGet(), "refunded");
        }
        return new GatewayResponse(404, "", "unknown payment reference: " + reference);
    }
}
