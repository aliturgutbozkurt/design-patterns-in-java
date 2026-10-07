package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.in.cli;

import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartView;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductView;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderView;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PriceQuote;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** The response texts of SPEC-capstone "Output formats". */
final class CliFormat {

    private CliFormat() {
    }

    static String product(ProductView p) {
        return String.join(" | ", p.sku().value(), p.name(), p.category().name(), p.type().name(),
                p.price().toPlainString(), String.valueOf(p.stock()));
    }

    static String cart(CartView cart) {
        String lines = cart.lines().isEmpty() ? "(empty)" : cart.lines().stream()
                .map(line -> line.sku().value() + " x" + line.quantity()).collect(Collectors.joining(", "));
        return cart.id().value() + " " + cart.customer().value() + " " + (cart.open() ? "open" : "closed") + " | "
                + lines + (cart.coupon().isEmpty() ? "" : " | coupon " + cart.coupon());
    }

    static String quote(PriceQuote quote) {
        List<String> lines = new ArrayList<>();
        lines.add("subtotal " + quote.subtotal().toPlainString());
        quote.discounts().forEach(d -> lines.add("- " + d.label() + " " + d.amount().toPlainString()));
        lines.add("shipping " + quote.shipping().toPlainString());
        lines.add("total " + quote.total().toPlainString());
        return String.join("\n", lines);
    }

    static String order(OrderView order) {
        return order.id().value() + " " + order.customer().value() + " " + order.status() + " "
                + order.total().toPlainString() + " | " + order.lines().stream()
                .map(line -> line.sku().value() + " x" + line.quantity()).collect(Collectors.joining(", "));
    }
}
