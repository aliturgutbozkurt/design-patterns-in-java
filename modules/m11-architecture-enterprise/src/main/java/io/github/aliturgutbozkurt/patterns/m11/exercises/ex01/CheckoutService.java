package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import java.util.List;

/** Assignment 01 — your application service: depends on the five ports only, never on an adapter class. */
public class CheckoutService implements CheckoutUseCase {

    private final ProductCatalog catalog;
    private final PaymentPort payments;
    private final OrderRepository orders;
    private final EventPublisher events;
    private final OrderIdGenerator ids;

    public CheckoutService(ProductCatalog catalog, PaymentPort payments, OrderRepository orders,
                           EventPublisher events, OrderIdGenerator ids) {
        // TODO(ex01): reject null collaborators.
        this.catalog = catalog;
        this.payments = payments;
        this.orders = orders;
        this.events = events;
        this.ids = ids;
    }

    @Override
    public CheckoutResult checkout(String customer, List<CartItem> cart) {
        // TODO(ex01): 1. empty cart? 2. per item in cart order: quantity <= 0? unknown product?
        //             3. merge duplicate SKUs (first position wins), then check stock.
        //             4. charge the total exactly once — declined? 5. only now: next id, save, then publish.
        throw new UnsupportedOperationException("TODO(ex01): implement checkout(String, List)");
    }
}
