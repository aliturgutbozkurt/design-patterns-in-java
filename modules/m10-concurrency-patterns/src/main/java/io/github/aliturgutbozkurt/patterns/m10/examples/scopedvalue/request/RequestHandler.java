package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request;

import java.util.Objects;

/**
 * The edge of the system: binds the request context once, for exactly the duration of the request.
 *
 * @see "m10 lesson, section Scoped Values"
 */
public final class RequestHandler {

    private final OrderService orders;

    public RequestHandler(OrderService orders) {
        this.orders = Objects.requireNonNull(orders, "orders");
    }

    /** Runs {@code work} with both values bound; a checked exception of {@code work} propagates unchanged. */
    public <T, X extends Throwable> T handle(String requestId, Principal principal,
            ScopedValue.CallableOp<T, X> work) throws X {
        return ScopedValue.where(RequestContext.REQUEST_ID, requestId)
                .where(RequestContext.PRINCIPAL, principal)
                .call(work);                     // unbound again when call returns, even on an exception
    }

    /** A request that places an order. */
    public String placeOrder(String requestId, Principal principal, String item) {
        return handle(requestId, principal, () -> orders.placeOrder(item));
    }
}
