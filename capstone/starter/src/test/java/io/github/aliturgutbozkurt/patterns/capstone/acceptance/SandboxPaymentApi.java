package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.ExternalPaymentApi;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.GatewayResponse;
import io.github.aliturgutbozkurt.patterns.capstone.api.sim.SimulatedPaymentApi;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Spy around the GIVEN {@link SimulatedPaymentApi} (same token semantics): logs every call with its arguments and can
 * make refunds fail with status 500. Thread-safe.
 */
public final class SandboxPaymentApi implements ExternalPaymentApi {

    /**
     * One call to the provider.
     *
     * @param operation {@code authorize} or {@code refund}
     * @param arguments the arguments in parameter order
     */
    public record Call(String operation, List<String> arguments) {
    }

    private final SimulatedPaymentApi provider = new SimulatedPaymentApi();
    private final List<Call> calls = new CopyOnWriteArrayList<>();
    private volatile boolean failRefunds;

    @Override
    public GatewayResponse authorize(String merchantId, String cardToken, String amount, String currency,
                                     String idempotencyKey) {
        calls.add(new Call("authorize", List.of(merchantId, cardToken, amount, currency, idempotencyKey)));
        return provider.authorize(merchantId, cardToken, amount, currency, idempotencyKey);
    }

    @Override
    public GatewayResponse refund(String merchantId, String reference, String amount, String currency) {
        calls.add(new Call("refund", List.of(merchantId, reference, amount, currency)));
        if (failRefunds) {
            return new GatewayResponse(500, "", "refund service down");
        }
        return provider.refund(merchantId, reference, amount, currency);
    }

    /** From now on every refund answers 500. */
    public void failRefunds() {
        failRefunds = true;
    }

    /** Every call so far, in order. */
    public List<Call> calls() {
        return List.copyOf(calls);
    }

    /** The calls of one operation, in order. */
    public List<Call> calls(String operation) {
        return calls.stream().filter(call -> call.operation().equals(operation)).toList();
    }
}
