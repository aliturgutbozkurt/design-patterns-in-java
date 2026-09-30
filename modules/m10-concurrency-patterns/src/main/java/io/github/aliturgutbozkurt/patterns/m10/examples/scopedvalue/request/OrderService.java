package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request;

import java.util.Objects;

/**
 * Business logic that never sees the request context as a parameter. Reserving stock runs "as system": a nested
 * {@code ScopedValue.where} rebinds {@link RequestContext#PRINCIPAL} for that block only, and the caller's
 * principal is back as soon as the block ends.
 *
 * @see "m10 lesson, section Scoped Values"
 */
public final class OrderService {

    private final AuditLog audit;

    public OrderService(AuditLog audit) {
        this.audit = Objects.requireNonNull(audit, "audit");
    }

    public String placeOrder(String item) {
        audit.record("place order " + item);
        ScopedValue.where(RequestContext.PRINCIPAL, Principal.SYSTEM)       // rebind for this block only
                .run(() -> audit.record("reserve stock for " + item));
        audit.record("confirm order " + item);                             // the outer principal again
        return "order for " + item + " placed by " + RequestContext.PRINCIPAL.get().name();
    }
}
