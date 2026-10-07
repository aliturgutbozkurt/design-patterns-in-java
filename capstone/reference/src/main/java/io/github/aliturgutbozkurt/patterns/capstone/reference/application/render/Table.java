package io.github.aliturgutbozkurt.patterns.capstone.reference.application.render;

import io.github.aliturgutbozkurt.patterns.capstone.api.report.DayTotal;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ProductSales;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.Report;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.Report.CustomerStatementReport;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.Report.DailySalesReport;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.Report.InventoryReport;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.Report.TopProductsReport;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.StatementLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.StockLine;
import java.util.List;
import java.util.Objects;

/**
 * A report as a table: title, column names, rows of text fields, and an optional total row (empty list = none). One
 * exhaustive {@code switch} with record patterns turns each sealed report into a table.
 *
 * @param title   the TEXT title line
 * @param columns the CSV header names
 * @param rows    one list of fields per row
 * @param total   the total row's fields, empty when the report has none
 * @see "capstone guide §1 Pattern map — Template Method"
 */
public record Table(String title, List<String> columns, List<List<String>> rows, List<String> total) {

    public Table {
        Objects.requireNonNull(title, "title");
        columns = List.copyOf(columns);
        rows = List.copyOf(rows);
        total = List.copyOf(total);
    }

    /** The table of a report. */
    public static Table of(Report report) {
        return switch (report) {
            case DailySalesReport(var days, var orders, var revenue) -> new Table(
                    "Daily sales " + days.getFirst().day() + " .. " + days.getLast().day(),
                    List.of("day", "orders", "revenue"), days.stream().map(Table::row).toList(),
                    List.of("Total", String.valueOf(orders), revenue.toPlainString()));
            case TopProductsReport(var rows) -> new Table("Top " + rows.size() + " products",
                    List.of("rank", "sku", "name", "units", "revenue"), rows.stream().map(Table::row).toList(),
                    List.of());
            case CustomerStatementReport(var customer, var lines, var spent) -> new Table(
                    "Statement for " + customer.value(), List.of("order", "date", "status", "total"),
                    lines.stream().map(Table::row).toList(), List.of("Total spent", spent.toPlainString()));
            case InventoryReport(var lines) -> new Table("Inventory", List.of("sku", "name", "stock", "low"),
                    lines.stream().map(Table::row).toList(), List.of());
        };
    }

    private static List<String> row(DayTotal day) {
        return List.of(day.day().toString(), String.valueOf(day.orders()), day.revenue().toPlainString());
    }

    private static List<String> row(ProductSales sales) {
        return List.of(String.valueOf(sales.rank()), sales.sku().value(), sales.name(), String.valueOf(sales.units()),
                sales.revenue().toPlainString());
    }

    private static List<String> row(StatementLine line) {
        return List.of(line.order().value(), line.date().toString(), line.status().name(),
                line.total().toPlainString());
    }

    private static List<String> row(StockLine line) {
        return List.of(line.sku().value(), line.name(), String.valueOf(line.stock()), line.low() ? "yes" : "no");
    }
}
