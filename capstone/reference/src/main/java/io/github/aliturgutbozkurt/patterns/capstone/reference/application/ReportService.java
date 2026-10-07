package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.DayTotal;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ProductSales;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.Report;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportFormat;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportRequest.CustomerStatement;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportRequest.DailySales;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportRequest.InventoryStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportRequest.TopProducts;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.StatementLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.StockLine;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.OrderRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.ProductRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.render.CsvRenderer;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.render.TextRenderer;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.PhysicalProduct;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Order;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderItem;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Feature F10: one exhaustive {@code switch} with record patterns over the sealed requests picks the computation, and
 * the sealed reports are rendered by the template-method renderers. No Visitor is needed: the compiler checks that
 * every request kind is handled.
 *
 * @see "capstone guide §2 Slice walkthrough — C6"
 */
@PatternRole(value = DesignPattern.PORTS_AND_ADAPTERS, role = "application service behind an inbound port")
public final class ReportService implements ReportUseCase {

    private final OrderRepository orders;
    private final ProductRepository products;
    private final Clock clock;
    private final int lowStockThreshold;

    public ReportService(OrderRepository orders, ProductRepository products, Clock clock, int lowStockThreshold) {
        this.orders = Objects.requireNonNull(orders, "orders");
        this.products = Objects.requireNonNull(products, "products");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.lowStockThreshold = lowStockThreshold;
    }

    @Override
    public Report run(ReportRequest request) {
        return switch (request) {
            case DailySales(var from, var to) -> dailySales(from, to);
            case TopProducts(var limit) -> topProducts(limit);
            case CustomerStatement(var customer) -> statement(customer);
            case InventoryStatus() -> inventory();
        };
    }

    @Override
    public String render(Report report, ReportFormat format) {
        return switch (format) {
            case TEXT -> new TextRenderer().render(report);
            case CSV -> new CsvRenderer().render(report);
        };
    }

    private Report dailySales(LocalDate from, LocalDate to) {
        List<Order> sold = notCancelled();
        List<DayTotal> days = from.datesUntil(to.plusDays(1)).map(day -> {
            List<Order> ofDay = sold.stream().filter(order -> dayOf(order).equals(day)).toList();
            return new DayTotal(day, ofDay.size(), total(ofDay));
        }).toList();
        int count = days.stream().mapToInt(DayTotal::orders).sum();
        Money revenue = days.stream().map(DayTotal::revenue).reduce(Money.ZERO, Money::plus);
        return new Report.DailySalesReport(days, count, revenue);
    }

    private Report topProducts(int limit) {
        Map<Sku, ProductSales> bySku = new LinkedHashMap<>();
        for (Order order : notCancelled()) {
            for (OrderItem item : order.items()) {
                ProductSales sold = bySku.getOrDefault(item.sku(), new ProductSales(0, item.sku(), item.name(), 0,
                        Money.ZERO));
                bySku.put(item.sku(), new ProductSales(0, item.sku(), sold.name(), sold.units() + item.quantity(),
                        sold.revenue().plus(item.unitPrice().times(item.quantity()))));
            }
        }
        List<ProductSales> ranked = bySku.values().stream()
                .sorted(Comparator.comparingInt(ProductSales::units).reversed()
                        .thenComparing(sales -> sales.sku().value()))
                .limit(limit).toList();
        return new Report.TopProductsReport(IntStream.range(0, ranked.size()).mapToObj(i -> {
            ProductSales row = ranked.get(i);
            return new ProductSales(i + 1, row.sku(), row.name(), row.units(), row.revenue());
        }).toList());
    }

    private Report statement(CustomerId customer) {
        List<Order> ofCustomer = orders.findAll().stream().filter(order -> order.customer().equals(customer)).toList();
        List<StatementLine> lines = ofCustomer.stream()
                .map(order -> new StatementLine(order.id(), dayOf(order), order.status(), order.total())).toList();
        Money spent = total(ofCustomer.stream().filter(order -> order.status() != OrderStatus.CANCELLED).toList());
        return new Report.CustomerStatementReport(customer, lines, spent);
    }

    private Report inventory() {
        List<StockLine> lines = new ArrayList<>();
        for (var product : products.findMatching(_ -> true)) {
            if (product instanceof PhysicalProduct physical) {
                lines.add(new StockLine(physical.sku(), physical.name(), physical.stock(),
                        physical.stock() < lowStockThreshold));
            }
        }
        return new Report.InventoryReport(lines);
    }

    private List<Order> notCancelled() {
        return orders.findAll().stream().filter(order -> order.status() != OrderStatus.CANCELLED).toList();
    }

    private LocalDate dayOf(Order order) {
        return LocalDate.ofInstant(order.placedAt(), clock.getZone());
    }

    private static Money total(List<Order> orders) {
        return orders.stream().map(Order::total).reduce(Money.ZERO, Money::plus);
    }
}
