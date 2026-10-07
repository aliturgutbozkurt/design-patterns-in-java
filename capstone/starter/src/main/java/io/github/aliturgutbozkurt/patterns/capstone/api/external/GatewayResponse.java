package io.github.aliturgutbozkurt.patterns.capstone.api.external;

import java.util.Objects;

/**
 * GIVEN — do not modify. The payment provider's answer.
 *
 * @param status  HTTP-like status: 200 approved, 402 declined, 5xx unavailable
 * @param reference the provider's reference ({@code txn-1}) when approved, otherwise {@code ""}
 * @param message a human-readable message
 * @see "capstone brief, Business rules — Checkout (F5, F6)"
 */
public record GatewayResponse(int status, String reference, String message) {

    public GatewayResponse {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(message, "message");
    }
}
