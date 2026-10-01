package io.github.aliturgutbozkurt.patterns.m11.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.CartItem;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.CheckoutResult;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.CheckoutUseCase;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.EventPublisher;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.Money;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.Order;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.OrderIdGenerator;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.OrderLine;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.OrderPlaced;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.OrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.PaymentPort;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.Product;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.ProductCatalog;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.Sku;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedMap;

/**
 * Reference solution, assignment 01: the checkout application service — validate, charge once, then id, save and
 * publish, in that order. Depends on ports only.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public final class CheckoutService implements CheckoutUseCase {

    private final ProductCatalog catalog;
    private final PaymentPort payments;
    private final OrderRepository orders;
    private final EventPublisher events;
    private final OrderIdGenerator ids;

    public CheckoutService(ProductCatalog catalog, PaymentPort payments, OrderRepository orders,
                           EventPublisher events, OrderIdGenerator ids) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.payments = Objects.requireNonNull(payments, "payments");
        this.orders = Objects.requireNonNull(orders, "orders");
        this.events = Objects.requireNonNull(events, "events");
        this.ids = Objects.requireNonNull(ids, "ids");
    }

    @Override
    public CheckoutResult checkout(String customer, List<CartItem> cart) {
        Objects.requireNonNull(customer, "customer");
        cart.forEach(item -> Objects.requireNonNull(item, "cart item"));
        if (cart.isEmpty()) {
            return new CheckoutResult.Rejected("empty cart");
        }
        Map<Sku, Product> products = new LinkedHashMap<>();
        SequencedMap<Sku, Integer> quantities = new LinkedHashMap<>(); // merged, at the first position
        for (CartItem item : cart) {
            if (item.quantity() <= 0) {
                return new CheckoutResult.Rejected("invalid quantity: " + item.sku());
            }
            var product = catalog.find(item.sku());
            if (product.isEmpty()) {
                return new CheckoutResult.Rejected("unknown product: " + item.sku());
            }
            products.put(item.sku(), product.get());
            quantities.merge(item.sku(), item.quantity(), Integer::sum);
        }
        List<OrderLine> lines = new ArrayList<>();
        for (var entry : quantities.entrySet()) {
            Product product = products.get(entry.getKey());
            if (product.stock() < entry.getValue()) {
                return new CheckoutResult.Rejected("insufficient stock: " + entry.getKey());
            }
            lines.add(new OrderLine(entry.getKey(), entry.getValue(), product.price()));
        }
        Money total = lines.stream().map(OrderLine::total).reduce(Money.ZERO, Money::plus);
        if (!payments.charge(customer, total)) {
            return new CheckoutResult.Rejected("payment declined");
        }
        var order = new Order(ids.next(), customer, lines, total);
        orders.save(order);                                               // may throw: then nothing is published
        events.publish(new OrderPlaced(order.id(), customer, total));
        return new CheckoutResult.Confirmed(order);
    }
}
