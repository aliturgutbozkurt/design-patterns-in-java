package io.github.aliturgutbozkurt.patterns.capstone.api.report;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import java.time.LocalDate;
import java.util.Objects;

/**
 * GIVEN — do not modify. The reports a user can ask for.
 *
 * @see "capstone brief §2.2 — Reports (F10)"
 */
public sealed interface ReportRequest {

    /**
     * Orders and revenue per day, for every day from {@code from} to {@code to} (inclusive), days without orders
     * included; cancelled orders excluded; the day of an order is its {@code placedAt} in the clock's time zone.
     *
     * @param from first day
     * @param to   last day, not before {@code from}
     */
    record DailySales(LocalDate from, LocalDate to) implements ReportRequest {
        public DailySales {
            Objects.requireNonNull(from, "from");
            Objects.requireNonNull(to, "to");
            if (to.isBefore(from)) {
                throw new IllegalArgumentException("range ends before it starts: " + from + " .. " + to);
            }
        }
    }

    /**
     * The best-selling products by units, then by SKU; revenue at list prices; cancelled orders excluded.
     *
     * @param limit the maximum number of rows, at least 1
     */
    record TopProducts(int limit) implements ReportRequest {
        public TopProducts {
            if (limit < 1) {
                throw new IllegalArgumentException("limit must be positive: " + limit);
            }
        }
    }

    /**
     * Every order of a customer in placement order; the total spent excludes cancelled orders.
     *
     * @param customer the customer
     */
    record CustomerStatement(CustomerId customer) implements ReportRequest {
        public CustomerStatement {
            Objects.requireNonNull(customer, "customer");
        }
    }

    /** The stock of every physical product, by SKU; {@code low} when the stock is below the low-stock threshold. */
    record InventoryStatus() implements ReportRequest {
    }
}
