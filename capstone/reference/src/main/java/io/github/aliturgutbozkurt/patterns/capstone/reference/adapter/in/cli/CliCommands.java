package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.in.cli;

import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartView;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.CatalogueUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductQuery;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutResult;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentReport;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.TransitionResult;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PricingUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportFormat;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportUseCase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Builds the CLI's command table, in the order {@code help} lists it. */
final class CliCommands {

    /** A command and its usage line. */
    record Entry(String usage, CliCommand command) {
    }

    private final CatalogueUseCase catalogue;
    private final CartUseCase carts;
    private final PricingUseCase pricing;
    private final CheckoutUseCase checkout;
    private final OrderUseCase orders;
    private final FulfilmentUseCase fulfilment;
    private final ReportUseCase reports;
    private final Map<String, Entry> table = new LinkedHashMap<>();

    CliCommands(CatalogueUseCase catalogue, CartUseCase carts, PricingUseCase pricing, CheckoutUseCase checkout,
                OrderUseCase orders, FulfilmentUseCase fulfilment, ReportUseCase reports) {
        this.catalogue = catalogue;
        this.carts = carts;
        this.pricing = pricing;
        this.checkout = checkout;
        this.orders = orders;
        this.fulfilment = fulfilment;
        this.reports = reports;
    }

    Map<String, Entry> table() {
        shoppingCommands();
        orderCommands();
        reportCommands();
        return Collections.unmodifiableMap(table);
    }

    private void shoppingCommands() {
        add("product list", "[CATEGORY]", this::productList);
        add("cart open", "<customer>", a -> "CART " + carts.open(a.exactly(1).customer(0)).value());
        add("cart add", "<cart> <sku> <qty>", a -> cart(carts.add(a.exactly(3).cart(0), a.sku(1), a.integer(2))));
        add("cart qty", "<cart> <sku> <qty>",
                a -> cart(carts.changeQuantity(a.exactly(3).cart(0), a.sku(1), a.integer(2))));
        add("cart remove", "<cart> <sku>", a -> cart(carts.remove(a.exactly(2).cart(0), a.sku(1))));
        add("cart coupon", "<cart> <code>", a -> cart(carts.applyCoupon(a.exactly(2).cart(0), a.text(1))));
        add("cart undo", "<cart>", a -> carts.undo(a.exactly(1).cart(0)) ? cart(carts.view(a.cart(0)))
                : "NOTHING TO UNDO");
        add("cart redo", "<cart>", a -> carts.redo(a.exactly(1).cart(0)) ? cart(carts.view(a.cart(0)))
                : "NOTHING TO REDO");
        add("cart show", "<cart>", a -> cart(carts.view(a.exactly(1).cart(0))));
    }

    private void orderCommands() {
        add("quote", "<cart>", a -> CliFormat.quote(pricing.quote(a.exactly(1).cart(0))));
        add("checkout", "<cart> <card-token> <recipient>;<street>;<city>;<postal-code>", this::checkout);
        add("order show", "<order>", a -> {
            OrderId id = a.exactly(1).order(0);
            return orders.find(id).map(CliFormat::order).orElse("ERROR unknown order: " + id.value());
        });
        add("order cancel", "<order> <reason…>", this::cancel);
        add("order deliver", "<order>", a -> transition(orders.markDelivered(a.exactly(1).order(0)), "DELIVERED"));
        add("fulfil", "", a -> fulfil(a.exactly(0)));
    }

    private void reportCommands() {
        add("report sales", "<from> <to> [--csv]", a -> {
            boolean csv = a.withCsvFlag(2);
            return report(CliArgs.parse(() -> new ReportRequest.DailySales(a.date(0), a.date(1))), csv);
        });
        add("report top", "<n> [--csv]", a -> {
            boolean csv = a.withCsvFlag(1);
            return report(CliArgs.parse(() -> new ReportRequest.TopProducts(a.integer(0))), csv);
        });
        add("report customer", "<customer> [--csv]", a -> {
            boolean csv = a.withCsvFlag(1);
            return report(new ReportRequest.CustomerStatement(a.customer(0)), csv);
        });
        add("report inventory", "[--csv]", a -> report(new ReportRequest.InventoryStatus(), a.withCsvFlag(0)));
    }

    private void add(String name, String arguments, CliCommand command) {
        table.put(name, new Entry(arguments.isEmpty() ? name : name + " " + arguments, command));
    }

    private String productList(CliArgs args) {
        ProductQuery query = ProductQuery.all();
        if (!args.tokens().isEmpty()) {
            Category category = CliArgs.parse(() -> Category.valueOf(args.exactly(1).text(0)));
            query = query.inCategory(category);
        }
        List<String> lines = catalogue.search(query).stream().map(CliFormat::product).toList();
        return lines.isEmpty() ? "NO PRODUCTS" : String.join("\n", lines);
    }

    private String checkout(CliArgs args) {
        String[] parts = args.rest().split("\\s+", 3);
        if (parts.length != 3) {
            throw new UsageException();
        }
        CheckoutResult result = checkout.checkout(new CheckoutRequest(new CartId(parts[0]),
                CliArgs.address(parts[2]), parts[1]));
        return switch (result) {
            case CheckoutResult.Placed(var order, var total, var reference) ->
                    "PLACED " + order.value() + " " + total.toPlainString() + " " + reference;
            case CheckoutResult.Rejected(var reasons) -> "REJECTED " + String.join("; ", reasons);
        };
    }

    private String cancel(CliArgs args) {
        String[] parts = args.rest().split("\\s+", 2);
        if (parts.length != 2) {
            throw new UsageException();
        }
        OrderId id = new CliArgs(List.of(parts[0]), parts[0]).order(0);
        return transition(orders.cancel(id, parts[1].strip()), "CANCELLED");
    }

    private static String transition(TransitionResult result, String done) {
        return switch (result) {
            case TransitionResult.Done(var order) -> done + " " + order.id().value();
            case TransitionResult.Refused(var reason) -> "REFUSED " + reason;
        };
    }

    private String fulfil(CliArgs args) {
        FulfilmentReport report = fulfilment.fulfilPaidOrders();
        Map<OrderId, String> lines = new TreeMap<>();
        report.shipped().forEach(id -> lines.put(id, "SHIPPED " + id.value() + " "
                + orders.find(id).map(order -> order.trackingCode()).orElse("")));
        report.failed().forEach(f -> lines.put(f.order(), "FAILED " + f.order().value() + " " + f.reason()));
        return lines.isEmpty() ? "NOTHING TO FULFIL" : String.join("\n", new ArrayList<>(lines.values()));
    }

    private String report(ReportRequest request, boolean csv) {
        return reports.render(reports.run(request), csv ? ReportFormat.CSV : ReportFormat.TEXT).stripTrailing();
    }

    private static String cart(CartView view) {
        return CliFormat.cart(view);
    }
}
