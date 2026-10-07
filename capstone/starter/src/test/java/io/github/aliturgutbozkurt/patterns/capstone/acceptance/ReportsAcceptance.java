package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.customer;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.item;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.money;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.orderId;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.sku;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductSpec;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.DayTotal;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ProductSales;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.Report;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportFormat;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.StatementLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.StockLine;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * F10 — reports as sealed request/report types, rendered as text and CSV. Scenario: 2026-11-16 alice buys the worked
 * example (order-1, 987.91); 2026-11-17 bob buys two mugs (order-2, 229.70) and alice buys a hub (order-3, 649.00)
 * and cancels it; then the clock moves to 2026-11-18. Product BOK-003 has a comma and quotes in its name.
 */
public abstract class ReportsAcceptance extends AcceptanceContract {

    private static final LocalDate NOV_15 = LocalDate.of(2026, 11, 15);
    private static final LocalDate NOV_16 = LocalDate.of(2026, 11, 16);
    private static final LocalDate NOV_17 = LocalDate.of(2026, 11, 17);
    private static final LocalDate NOV_18 = LocalDate.of(2026, 11, 18);
    private static final LocalDate NOV_19 = LocalDate.of(2026, 11, 19);

    private void scenario() {
        shop().catalogue().add(new ProductSpec(sku("BOK-003"), "Patterns, \"Applied\"", Category.BOOKS,
                ProductType.PHYSICAL, money("150.00"), 9));
        CartId cart = kit().cartWith("alice",
                item("BOK-001", 2), item("BOK-002", 1), item("TOY-001", 3), item("DIG-001", 1));
        shop().carts().applyCoupon(cart, "AUTUMN5");
        ShopTestKit.placed(kit().checkout(cart));
        kit().clock().advance(Duration.ofDays(1));
        kit().placeOrder("bob", item("HOM-001", 2));
        OrderId third = kit().placeOrder("alice", item("ELE-001", 1));
        shop().orders().cancel(third, "duplicate order");
        kit().clock().advance(Duration.ofDays(1));
    }

    private Report run(ReportRequest request) {
        return shop().reports().run(request);
    }

    private static DayTotal day(LocalDate day, int orders, String revenue) {
        return new DayTotal(day, orders, money(revenue));
    }

    private static ProductSales sales(int rank, String sku, String name, int units, String revenue) {
        return new ProductSales(rank, sku(sku), name, units, money(revenue));
    }

    private static StockLine stock(String sku, String name, int stock, boolean low) {
        return new StockLine(sku(sku), name, stock, low);
    }

    @Test
    void dailySalesCoversEveryDayInRangeAndExcludesCancelled() {
        scenario();

        assertThat(run(new ReportRequest.DailySales(NOV_15, NOV_18))).isEqualTo(new Report.DailySalesReport(List.of(
                day(NOV_15, 0, "0.00"), day(NOV_16, 1, "987.91"), day(NOV_17, 1, "229.70"), day(NOV_18, 0, "0.00")),
                2, money("1217.61")));

        kit().clock().advanceTo(ZonedDateTime.parse("2026-11-19T01:30+03:00[Europe/Istanbul]")); // 22:30 UTC on the 18th
        kit().placeOrder("carol", item("DIG-001", 1));
        assertThat(run(new ReportRequest.DailySales(NOV_18, NOV_19))).as("days are in the clock's time zone")
                .isEqualTo(new Report.DailySalesReport(List.of(day(NOV_18, 0, "0.00"), day(NOV_19, 1, "89.91")),
                        1, money("89.91")));
    }

    @Test
    void topProductsRankByUnitsThenSku() {
        scenario();

        assertThat(run(new ReportRequest.TopProducts(3))).isEqualTo(new Report.TopProductsReport(List.of(
                sales(1, "TOY-001", "Pattern Puzzle", 3, "360.00"),
                sales(2, "BOK-001", "Design Patterns Handbook", 2, "500.00"),
                sales(3, "HOM-001", "Hexagon Mug", 2, "179.80"))));
        assertThat(run(new ReportRequest.TopProducts(10))).as("only sold products; cancelled orders excluded")
                .isEqualTo(new Report.TopProductsReport(List.of(
                        sales(1, "TOY-001", "Pattern Puzzle", 3, "360.00"),
                        sales(2, "BOK-001", "Design Patterns Handbook", 2, "500.00"),
                        sales(3, "HOM-001", "Hexagon Mug", 2, "179.80"),
                        sales(4, "BOK-002", "Java 27 in Action", 1, "400.00"),
                        sales(5, "DIG-001", "E-book Bundle", 1, "99.90"))));
    }

    @Test
    void customerStatementListsOrdersAndTotalSpent() {
        scenario();

        assertThat(run(new ReportRequest.CustomerStatement(customer("alice")))).isEqualTo(
                new Report.CustomerStatementReport(customer("alice"), List.of(
                        new StatementLine(orderId("order-1"), NOV_16, OrderStatus.PAID, money("987.91")),
                        new StatementLine(orderId("order-3"), NOV_17, OrderStatus.CANCELLED, money("649.00"))),
                        money("987.91")));
        assertThat(run(new ReportRequest.CustomerStatement(customer("dave"))))
                .isEqualTo(new Report.CustomerStatementReport(customer("dave"), List.of(), Money.ZERO));
    }

    @Test
    void inventoryReportFlagsLowStock() {
        scenario();

        assertThat(run(new ReportRequest.InventoryStatus())).isEqualTo(new Report.InventoryReport(List.of(
                stock("BOK-001", "Design Patterns Handbook", 18, false),
                stock("BOK-002", "Java 27 in Action", 7, false),
                stock("BOK-003", "Patterns, \"Applied\"", 9, false),
                stock("ELE-001", "USB-C Hub", 10, false),
                stock("HOM-001", "Hexagon Mug", 48, false),
                stock("TOY-001", "Pattern Puzzle", 3, true))));

        shop().catalogue().restock(sku("TOY-001"), 2);
        assertThat(((Report.InventoryReport) run(new ReportRequest.InventoryStatus())).lines().getLast())
                .as("5 is not below the threshold 5").isEqualTo(stock("TOY-001", "Pattern Puzzle", 5, false));
    }

    @Test
    void textRenderingMatchesTheSpecifiedLayout() {
        scenario();

        assertThat(render(new ReportRequest.DailySales(NOV_16, NOV_18), ReportFormat.TEXT))
                .isEqualTo(ExpectedOutput.read("report-daily-sales.txt"));
        assertThat(render(new ReportRequest.TopProducts(3), ReportFormat.TEXT))
                .isEqualTo(ExpectedOutput.read("report-top-products.txt"));
        assertThat(render(new ReportRequest.CustomerStatement(customer("alice")), ReportFormat.TEXT))
                .isEqualTo(ExpectedOutput.read("report-customer-statement.txt"));
        assertThat(render(new ReportRequest.InventoryStatus(), ReportFormat.TEXT))
                .isEqualTo(ExpectedOutput.read("report-inventory.txt"));
    }

    @Test
    void csvRenderingHasHeaderAndOneRowPerEntry() {
        scenario();

        assertThat(render(new ReportRequest.DailySales(NOV_16, NOV_18), ReportFormat.CSV))
                .isEqualTo(ExpectedOutput.read("report-daily-sales.csv.txt"));
        assertThat(render(new ReportRequest.TopProducts(3), ReportFormat.CSV))
                .isEqualTo(ExpectedOutput.read("report-top-products.csv.txt"));
        assertThat(render(new ReportRequest.CustomerStatement(customer("alice")), ReportFormat.CSV))
                .isEqualTo(ExpectedOutput.read("report-customer-statement.csv.txt"));
        assertThat(render(new ReportRequest.InventoryStatus(), ReportFormat.CSV))
                .as("RFC 4180 quoting of a name with a comma and quotes")
                .isEqualTo(ExpectedOutput.read("report-inventory.csv.txt"));
    }

    private String render(ReportRequest request, ReportFormat format) {
        return shop().reports().render(run(request), format);
    }
}
