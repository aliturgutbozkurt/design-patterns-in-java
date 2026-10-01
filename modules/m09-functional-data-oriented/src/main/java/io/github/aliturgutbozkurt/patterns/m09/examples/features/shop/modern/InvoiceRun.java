package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern;

import io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern.LineItem.DigitalItem;
import io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern.LineItem.GiftCard;
import io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern.LineItem.PhysicalItem;
import java.util.ArrayList;
import java.util.List;

/**
 * The invoice run in "functional + data-oriented" style, with the same output as the classic package.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public final class InvoiceRun {

    /** Every top-level type of this package (the test checks this list against the compiled classes). */
    public static final List<Class<?>> TYPES =
            List.of(LineItem.class, Order.class, Invoice.class, Invoicing.class, InvoiceRun.class, InvoiceRunDemo.class);

    private InvoiceRun() {}

    public static List<Order> sampleOrders() {
        return List.of(
                new Order("A-1", "Ada", List.of(new PhysicalItem("MUG-0001", 12_50, 2, 350),
                        new DigitalItem("EBOOK-0007", 9_99)), "TEN"),
                new Order("A-2", "Grace", List.of(new GiftCard("GC-50", 50_00)), ""),
                new Order("A-3", "Linus", List.of(new PhysicalItem("LAMP-0003", 60_00, 1, 1200),
                        new PhysicalItem("TEE-0002", 20_00, 3, 200)), "BULK"),
                new Order("A-4", "Alan", List.of(new DigitalItem("EBOOK-0008", 14_99),
                        new DigitalItem("COURSE-0009", 49_00)), "BULK"));
    }

    /** All invoices, then the log of the post-actions in the order they ran. */
    public static String run(List<Order> orders) {
        var out = new StringBuilder();
        List<String> log = new ArrayList<>();
        List<Runnable> actions = new ArrayList<>();
        for (Order order : orders) {
            Invoice invoice = Invoicing.invoice(order);
            out.append(Invoicing.plainText(invoice)).append('\n');
            actions.addAll(Invoicing.postActions(invoice, log));
        }
        actions.forEach(Runnable::run);
        out.append("-- post-actions\n");
        log.forEach(line -> out.append(line).append('\n'));
        return out.toString();
    }
}
