package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Facade (m04) over the extracted parts: the same public behaviour as the old {@code OrderManager.checkout}, now
 * only orchestration — each responsibility lives in its own small class.
 *
 * @see "m11 lesson, section Anti-patterns — god class"
 */
public final class CheckoutFacade {

    private final OrderValidator validator;
    private final OrderRepository orders;
    private final ConfirmationMailer mailer;
    private final Consumer<String> log;

    public CheckoutFacade(OrderValidator validator, OrderRepository orders, ConfirmationMailer mailer,
                          Consumer<String> log) {
        this.validator = Objects.requireNonNull(validator, "validator");
        this.orders = Objects.requireNonNull(orders, "orders");
        this.mailer = Objects.requireNonNull(mailer, "mailer");
        this.log = Objects.requireNonNull(log, "log");
    }

    /** Returns {@code OK <id> <total>} or {@code REJECTED <reason>}, exactly like the god class. */
    public String checkout(CheckoutRequest request) {
        Optional<String> problem = validator.firstProblem(request);
        if (problem.isPresent()) {
            log.accept("rejected " + request.customer() + ": " + problem.get());
            return "REJECTED " + problem.get();
        }
        long total = PricingPolicy.forCustomerType(request.customerType()).orElseThrow()
                .totalCents(request.quantity(), request.unitPriceCents());
        var order = new PlacedOrder(orders.nextId(), request.customer(), request.quantity(), total);
        orders.save(order);
        mailer.sendConfirmation(order);
        String formatted = BigDecimal.valueOf(total, 2).toPlainString();
        log.accept("checkout " + order.customer() + " " + order.id() + " " + formatted);
        return "OK " + order.id() + " " + formatted;
    }
}
