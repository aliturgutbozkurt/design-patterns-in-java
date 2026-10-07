package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutResult;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutResult.Placed;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutResult.Rejected;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderPaid;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.OrderPlaced;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Address;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.Changes;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.UnitOfWork;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.CartRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.OrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentOutcome;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentOutcome.Approved;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentOutcome.Declined;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentOutcome.Unavailable;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PaymentPort;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.Cart;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.checkout.CandidateLine;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.checkout.CheckoutCandidate;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.checkout.CheckoutRules;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.ids.SequentialIds;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Order;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderItem;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderLifecycle;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderState;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Transition;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.BasketLine;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PriceSheet;
import java.time.Clock;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Features F5 and F6: one simple call over validation, pricing, payment, stock, orders and carts — validate → quote →
 * charge → commit → dispatch (after the unit of work). If the commit fails after the card was charged, the charge is
 * refunded before the failure propagates (compensation; adapted from modules/m05-…/facade/checkout/CheckoutFacade.java)
 * and the unit of work undoes the writes already made.
 *
 * @see "capstone guide, Pattern map — Facade"
 */
@PatternRole(value = DesignPattern.FACADE, role = "facade (with compensation)")
public final class CheckoutService implements CheckoutUseCase {

    private final CartRepository carts;
    private final OrderRepository orders;
    private final PricingService pricing;
    private final PaymentPort payments;
    private final Inventory inventory;
    private final SequentialIds<OrderId> ids;
    private final Clock clock;
    private final UnitOfWork unitOfWork;

    public CheckoutService(CartRepository carts, OrderRepository orders, PricingService pricing, PaymentPort payments,
                           Inventory inventory, SequentialIds<OrderId> ids, Clock clock, UnitOfWork unitOfWork) {
        this.carts = Objects.requireNonNull(carts, "carts");
        this.orders = Objects.requireNonNull(orders, "orders");
        this.pricing = Objects.requireNonNull(pricing, "pricing");
        this.payments = Objects.requireNonNull(payments, "payments");
        this.inventory = Objects.requireNonNull(inventory, "inventory");
        this.ids = Objects.requireNonNull(ids, "ids");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.unitOfWork = Objects.requireNonNull(unitOfWork, "unitOfWork");
    }

    @Override
    public CheckoutResult checkout(CheckoutRequest request) {
        return unitOfWork.run(changes -> {
            Cart cart = carts.find(request.cart())
                    .orElseThrow(() -> new NoSuchElementException("unknown cart: " + request.cart().value()))
                    .requireOpen();
            PriceSheet sheet = pricing.priceOf(cart);
            List<String> reasons = CheckoutRules.standard().check(candidate(cart, sheet, request));
            if (!reasons.isEmpty()) {
                return new Rejected(reasons);
            }
            return switch (charge(sheet.total(), request)) {
                case Approved(var reference) -> placeOrRefund(cart, sheet, request.shippingAddress(), reference,
                        changes);
                case Declined _ -> new Rejected(List.of("payment declined"));
                case Unavailable _ -> new Rejected(List.of("payment unavailable"));
            };
        });
    }

    private CheckoutCandidate candidate(Cart cart, PriceSheet sheet, CheckoutRequest request) {
        List<CandidateLine> lines = sheet.lines().stream().map(line -> line.item())
                .map(item -> new CandidateLine(item.sku(), item.type(), item.quantity(), inventory.stockOf(item.sku())))
                .toList();
        return new CheckoutCandidate(lines, request.shippingAddress(), cart.coupon(), pricing.couponExpired(cart),
                request.cardToken());
    }

    private PaymentOutcome charge(Money total, CheckoutRequest request) {
        return total.isZero() ? new Approved(OrderState.FREE)
                : payments.charge(total, request.cardToken(), request.cart().value());
    }

    private CheckoutResult placeOrRefund(Cart cart, PriceSheet sheet, Address address, String reference,
                                         Changes changes) {
        try {
            return place(cart, sheet, address, reference, changes);
        } catch (RuntimeException failure) {
            if (!OrderState.FREE.equals(reference)) {
                PaymentOutcome refund = payments.refund(reference, sheet.total());
                if (!(refund instanceof Approved)) {
                    failure.addSuppressed(new IllegalStateException("compensating refund failed: " + refund));
                }
            }
            throw failure;
        }
    }

    private Placed place(Cart cart, PriceSheet sheet, Address address, String reference, Changes changes) {
        Order.Builder builder = Order.builder().id(ids.next()).customer(cart.customer()).total(sheet.total())
                .shipTo(address).placedAt(clock.instant());
        sheet.lines().forEach(line -> builder.item(orderItem(line.item())));
        Order placed = builder.build();
        Order paid = switch (OrderLifecycle.pay(placed.state(), reference)) {
            case Transition.Allowed allowed -> placed.after(allowed, placed.placedAt());
            case Transition.Refused refused -> throw new IllegalStateException(refused.reason());
        };
        orders.save(paid);
        changes.onRollback(() -> orders.remove(paid.id()));
        carts.save(cart.closed());
        changes.onRollback(() -> carts.save(cart));
        changes.raise(new OrderPlaced(paid.id(), paid.customer(), paid.total()));
        changes.raise(new OrderPaid(paid.id(), reference));
        inventory.reserve(paid.items(), changes);
        return new Placed(paid.id(), paid.total(), reference);
    }

    private static OrderItem orderItem(BasketLine line) {
        return new OrderItem(line.sku(), line.name(), line.type(), line.quantity(), line.unitPrice());
    }
}
