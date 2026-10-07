package io.github.aliturgutbozkurt.patterns.capstone.api.report;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import java.time.LocalDate;
import java.util.Objects;

/**
 * GIVEN — do not modify. One day of a daily sales report.
 *
 * @param day     the day
 * @param orders  orders placed that day (not cancelled)
 * @param revenue sum of their totals
 * @see "capstone brief §2.2 — Reports (F10)"
 */
public record DayTotal(LocalDate day, int orders, Money revenue) {

    public DayTotal {
        Objects.requireNonNull(day, "day");
        Objects.requireNonNull(revenue, "revenue");
    }
}
