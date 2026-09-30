package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

import java.util.ArrayList;
import java.util.List;

/**
 * The invoice run in "GoF-heavy" style: strategies, visitors, a template method and command objects.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public final class InvoiceRun {

    /** Every top-level type of this package (the test checks this list against the compiled classes). */
    public static final List<Class<?>> TYPES = List.of(LineItem.class, PhysicalItem.class, DigitalItem.class,
            GiftCard.class, LineItemVisitor.class, TaxVisitor.class, WeightVisitor.class, DiscountStrategy.class,
            NoDiscount.class, PercentDiscount.class, BulkDiscount.class, DiscountStrategies.class, Order.class,
            InvoiceFormatter.class, PlainInvoiceFormatter.class, PostAction.class, EmailInvoice.class,
            ArchiveInvoice.class, BookShipping.class, InvoiceRun.class, InvoiceRunDemo.class);

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
        InvoiceFormatter formatter = new PlainInvoiceFormatter();
        var out = new StringBuilder();
        List<PostAction> actions = new ArrayList<>();
        for (Order order : orders) {
            out.append(formatter.format(order)).append('\n');
            actions.add(new EmailInvoice(order));
            actions.add(new ArchiveInvoice(order));
            int grams = 0;
            for (LineItem item : order.getItems()) {
                grams += item.accept(new WeightVisitor());
            }
            if (grams > 0) {
                actions.add(new BookShipping(order, grams));
            }
        }
        List<String> log = new ArrayList<>();
        for (PostAction action : actions) {
            action.execute(log);
        }
        out.append("-- post-actions\n");
        log.forEach(line -> out.append(line).append('\n'));
        return out.toString();
    }
}
