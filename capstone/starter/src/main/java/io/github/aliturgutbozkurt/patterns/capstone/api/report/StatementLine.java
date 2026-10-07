package io.github.aliturgutbozkurt.patterns.capstone.api.report;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import java.time.LocalDate;
import java.util.Objects;

/**
 * GIVEN — do not modify. One order in a customer statement.
 *
 * @param order  the order
 * @param date   the day it was placed (clock's time zone)
 * @param status its current status
 * @param total  the amount charged
 * @see "capstone brief, Business rules — Reports (F10)"
 */
public record StatementLine(OrderId order, LocalDate date, OrderStatus status, Money total) {

    public StatementLine {
        Objects.requireNonNull(order, "order");
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(total, "total");
    }
}
