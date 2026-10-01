package io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes;

import java.math.BigDecimal;
import java.util.function.Supplier;

/**
 * One request's scope: per-request objects are created lazily on first use and shared until the scope is closed.
 * Obtained from {@link ShopCompositionRoot#beginRequest()}; use it in try-with-resources.
 *
 * @see "m11 lesson, section Dependency Injection — lifetimes"
 */
public final class RequestScope implements AutoCloseable {

    private final int number;
    private final PriceList priceList;
    private final Supplier<Basket> newBasket;
    private final AuditFile orderAudit;
    private Basket basket; // per-request: created on first use, then shared within this request
    private boolean closed;

    RequestScope(int number, PriceList priceList, Supplier<Basket> newBasket, AuditFile orderAudit) {
        this.number = number;
        this.priceList = priceList;
        this.newBasket = newBasket;
        this.orderAudit = orderAudit;
    }

    /** The request number (1, 2, …). */
    public int number() {
        return number;
    }

    /** The application-lifetime price list: the same instance in every request. */
    public PriceList priceList() {
        ensureOpen();
        return priceList;
    }

    /** This request's basket: the same instance for the whole request. */
    public Basket basket() {
        ensureOpen();
        if (basket == null) {
            basket = newBasket.get();
        }
        return basket;
    }

    /** Pays for the basket and writes the total to the order audit file. */
    public BigDecimal checkout() {
        Basket current = basket();
        BigDecimal total = current.total();
        orderAudit.write("request " + number + " paid " + total + " for " + current.items());
        return total;
    }

    /** Ends the request; per-request objects become unreachable. Idempotent. */
    @Override
    public void close() {
        closed = true;
        basket = null;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("request " + number + " is closed");
        }
    }
}
