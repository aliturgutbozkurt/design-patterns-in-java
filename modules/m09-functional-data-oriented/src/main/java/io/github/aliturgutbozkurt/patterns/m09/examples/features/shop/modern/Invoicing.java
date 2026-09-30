package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern;

import io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern.LineItem.DigitalItem;
import io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern.LineItem.GiftCard;
import io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern.LineItem.PhysicalItem;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * The invoice run as functions: {@code switch}es instead of visitors, a map of functions instead of discount
 * strategy classes, a formatter that takes its steps as arguments instead of a template-method base class, and
 * {@link Runnable}s instead of command objects.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public final class Invoicing {

    /** Discount code → (subtotal → discount). */
    static final Map<String, Function<Long, Long>> DISCOUNTS = Map.of(
            "", _ -> 0L,
            "TEN", subtotal -> subtotal * 10 / 100,
            "BULK", subtotal -> subtotal >= 100_00 ? subtotal * 5 / 100 : 0L);

    private Invoicing() {}

    static long amount(LineItem item) {
        return switch (item) {
            case PhysicalItem(_, var unit, var qty, _) -> unit * qty;
            case DigitalItem(_, var price) -> price;
            case GiftCard(_, var value) -> value;
        };
    }

    static long tax(LineItem item) {
        return switch (item) {
            case PhysicalItem p -> amount(p) * 20 / 100;
            case DigitalItem d -> amount(d) * 10 / 100;
            case GiftCard _ -> 0;
        };
    }

    static int grams(LineItem item) {
        return switch (item) {
            case PhysicalItem(_, _, var qty, var each) -> qty * each;
            case DigitalItem _, GiftCard _ -> 0;
        };
    }

    static String describe(LineItem item) {
        return switch (item) {
            case PhysicalItem(var sku, _, var qty, _) -> sku + " x" + qty;
            case DigitalItem(var sku, _) -> sku + " (download)";
            case GiftCard(var code, _) -> "gift card " + code;
        };
    }

    public static Invoice invoice(Order order) {
        long subtotal = order.items().stream().mapToLong(Invoicing::amount).sum();
        long discount = Optional.ofNullable(DISCOUNTS.get(order.discountCode()))
                .orElseThrow(() -> new IllegalArgumentException("unknown discount code: " + order.discountCode()))
                .apply(subtotal);
        String name = order.discountCode().isBlank() ? "none" : order.discountCode();
        return new Invoice(order, subtotal, name, discount,
                order.items().stream().mapToLong(Invoicing::tax).sum(),
                order.items().stream().mapToInt(Invoicing::grams).sum());
    }

    /** The template as a function: the skeleton is fixed, the look is passed in. */
    static String format(Invoice invoice, Function<Order, String> header, Function<Line, String> line,
                         Function<Integer, String> footer) {
        var out = new StringBuilder(header.apply(invoice.order()));
        invoice.order().items().forEach(item -> out.append(line.apply(new Line(describe(item), amount(item)))));
        out.append(line.apply(new Line("subtotal", invoice.subtotalCents())));
        out.append(line.apply(new Line("discount " + invoice.discountName(), -invoice.discountCents())));
        out.append(line.apply(new Line("tax", invoice.taxCents())));
        out.append(line.apply(new Line("total", invoice.totalCents())));
        return out.append(footer.apply(invoice.grams())).toString();
    }

    /** One printed line of an invoice. */
    record Line(String label, long cents) {}

    public static String plainText(Invoice invoice) {
        return format(invoice,
                order -> "INVOICE " + order.id() + " for " + order.customer() + "\n",
                line -> String.format(Locale.ROOT, "  %-24s %9s\n", line.label(), money(line.cents())),
                grams -> (grams > 0 ? "  shipping weight " + grams + " g" : "  nothing to ship") + "\n");
    }

    /** The post-actions of one invoice, as plain {@code Runnable}s that write to {@code log}. */
    public static List<Runnable> postActions(Invoice invoice, List<String> log) {
        Order order = invoice.order();
        Runnable email = () -> log.add("email invoice " + order.id() + " to " + order.customer());
        Runnable archive = () -> log.add("archive invoice " + order.id());
        Runnable ship = () -> log.add("book shipping " + order.id() + " (" + invoice.grams() + " g)");
        return invoice.grams() > 0 ? List.of(email, archive, ship) : List.of(email, archive);
    }

    private static String money(long cents) {
        return String.format(Locale.ROOT, "%s%d.%02d", cents < 0 ? "-" : "", Math.abs(cents) / 100,
                Math.abs(cents) % 100);
    }
}
