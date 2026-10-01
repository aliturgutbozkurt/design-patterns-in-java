package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderCommand;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderCommand.LineRequest;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderResult;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderUseCase;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.EventPublisher;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.OrderIds;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.OrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.PaymentPort;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.ProductCatalog;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderLine;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The PatternShop use case: validate against the catalogue, charge, save, then publish. Depends on five ports and
 * the domain — never on an adapter — so the composition root can run it on memory or on files.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public final class PlaceOrderService implements PlaceOrderUseCase {

    private final ProductCatalog catalog;
    private final PaymentPort payments;
    private final OrderRepository orders;
    private final EventPublisher events;
    private final OrderIds ids;

    public PlaceOrderService(ProductCatalog catalog, PaymentPort payments, OrderRepository orders,
                             EventPublisher events, OrderIds ids) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.payments = Objects.requireNonNull(payments, "payments");
        this.orders = Objects.requireNonNull(orders, "orders");
        this.events = Objects.requireNonNull(events, "events");
        this.ids = Objects.requireNonNull(ids, "ids");
    }

    @Override
    public PlaceOrderResult place(PlaceOrderCommand command) {
        Objects.requireNonNull(command, "command");
        if (command.customer().isBlank()) {
            return new PlaceOrderResult.Rejected("missing customer");
        }
        if (command.lines().isEmpty()) {
            return new PlaceOrderResult.Rejected("empty order");
        }
        List<OrderLine> lines = new ArrayList<>();
        for (LineRequest request : command.lines()) {
            if (request.quantity() <= 0) {
                return new PlaceOrderResult.Rejected("invalid quantity: " + request.sku());
            }
            Optional<Money> price = catalog.priceOf(request.sku());
            if (price.isEmpty()) {
                return new PlaceOrderResult.Rejected("unknown product: " + request.sku());
            }
            lines.add(new OrderLine(request.sku(), request.quantity(), price.get()));
        }
        if (!payments.charge(command.customer(), Order.totalOf(lines))) {
            return new PlaceOrderResult.Rejected("payment declined");
        }
        Order order = Order.place(ids.next(), command.customer(), lines);
        orders.save(order);                          // commit first …
        order.pullEvents().forEach(events::publish); // … then tell the world
        return new PlaceOrderResult.Placed(order.id(), order.total());
    }
}
