package io.github.aliturgutbozkurt.patterns.capstone.api.report;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import java.util.List;
import java.util.Objects;

/**
 * GIVEN — do not modify. The reports, one per {@link ReportRequest}.
 *
 * @see "capstone brief §2.2 — Reports (F10); SPEC-capstone, Output formats"
 */
public sealed interface Report {

    /**
     * Answer to {@link ReportRequest.DailySales}.
     *
     * @param days    one row per day of the range, oldest first
     * @param orders  orders in the range
     * @param revenue sum of their totals
     */
    record DailySalesReport(List<DayTotal> days, int orders, Money revenue) implements Report {
        public DailySalesReport {
            days = List.copyOf(days);
            Objects.requireNonNull(revenue, "revenue");
            if (days.isEmpty()) {
                throw new IllegalArgumentException("a daily sales report has at least one day");
            }
        }
    }

    /**
     * Answer to {@link ReportRequest.TopProducts}.
     *
     * @param rows ranked rows, rank 1 first; at most the requested limit
     */
    record TopProductsReport(List<ProductSales> rows) implements Report {
        public TopProductsReport {
            rows = List.copyOf(rows);
        }
    }

    /**
     * Answer to {@link ReportRequest.CustomerStatement}.
     *
     * @param customer   the customer
     * @param lines      their orders in placement order
     * @param totalSpent sum of the totals of the orders that are not cancelled
     */
    record CustomerStatementReport(CustomerId customer, List<StatementLine> lines, Money totalSpent)
            implements Report {
        public CustomerStatementReport {
            Objects.requireNonNull(customer, "customer");
            lines = List.copyOf(lines);
            Objects.requireNonNull(totalSpent, "totalSpent");
        }
    }

    /**
     * Answer to {@link ReportRequest.InventoryStatus}.
     *
     * @param lines physical products by SKU
     */
    record InventoryReport(List<StockLine> lines) implements Report {
        public InventoryReport {
            lines = List.copyOf(lines);
        }
    }
}
