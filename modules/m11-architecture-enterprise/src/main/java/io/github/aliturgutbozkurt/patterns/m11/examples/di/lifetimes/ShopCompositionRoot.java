package io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Composition root with explicit lifetimes: <b>application</b> objects are plain fields created once, <b>per-request</b>
 * objects live in a {@link RequestScope}, <b>transient</b> objects are created on every lookup. The root owns every
 * resource it creates and closes them in reverse creation order.
 *
 * @see "m11 lesson, section Dependency Injection — lifetimes"
 */
public final class ShopCompositionRoot implements AutoCloseable {

    private static final Map<String, BigDecimal> PRICES = Map.of(
            "book", new BigDecimal("20.00"),
            "pen", new BigDecimal("7.00"),
            "mug", new BigDecimal("12.50"));

    private final Deque<AutoCloseable> owned = new ArrayDeque<>(); // closed last-in, first-out
    private final PriceList priceList;                              // application lifetime
    private final AuditFile orderAudit;                             // application lifetime, owned resource
    private final AuditFile accessAudit;                            // application lifetime, owned resource
    private int requests;
    private int logs;
    private boolean closed;

    private ShopCompositionRoot(Clock clock, Consumer<String> disk) {
        priceList = new PriceList(PRICES);
        orderAudit = own(new AuditFile("orders.audit", clock, disk));
        accessAudit = own(new AuditFile("access.audit", clock, disk));
    }

    /** Builds the application graph; {@code disk} stands in for the file system the audit files write to. */
    public static ShopCompositionRoot production(Clock clock, Consumer<String> disk) {
        return new ShopCompositionRoot(Objects.requireNonNull(clock, "clock"), Objects.requireNonNull(disk, "disk"));
    }

    /** Application lifetime: always the same instance. */
    public PriceList priceList() {
        ensureOpen();
        return priceList;
    }

    /** Per-request lifetime: opens a new scope; close it when the request ends. */
    public RequestScope beginRequest() {
        ensureOpen();
        return new RequestScope(++requests, priceList, () -> new Basket(priceList), orderAudit);
    }

    /** Transient lifetime: a new instance on every call. */
    public RequestLog requestLog() {
        ensureOpen();
        return new RequestLog(++logs, accessAudit);
    }

    /** Closes every owned resource in reverse creation order; later calls do nothing. */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        IllegalStateException failure = null;
        while (!owned.isEmpty()) {
            try {
                owned.removeLast().close();
            } catch (Exception e) { // keep closing the others, report every failure
                if (failure == null) {
                    failure = new IllegalStateException("closing the composition root failed", e);
                } else {
                    failure.addSuppressed(e);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    private <T extends AutoCloseable> T own(T resource) {
        owned.addLast(resource);
        return resource;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("composition root is closed");
        }
    }
}
