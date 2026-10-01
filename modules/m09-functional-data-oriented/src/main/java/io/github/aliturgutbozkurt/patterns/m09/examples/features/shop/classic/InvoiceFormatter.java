package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/**
 * Template Method: the invoice algorithm is fixed here; subclasses decide how each part looks.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public abstract class InvoiceFormatter {

    public final String format(Order order) {
        var out = new StringBuilder(header(order));
        long subtotal = 0;
        long tax = 0;
        int grams = 0;
        for (LineItem item : order.getItems()) {
            out.append(line(item.describe(), item.amountCents()));
            subtotal += item.amountCents();
            tax += item.accept(new TaxVisitor());
            grams += item.accept(new WeightVisitor());
        }
        DiscountStrategy discount = DiscountStrategies.forCode(order.getDiscountCode());
        long off = discount.discountCents(subtotal);
        out.append(line("subtotal", subtotal));
        out.append(line("discount " + discount.name(), -off));
        out.append(line("tax", tax));
        out.append(line("total", subtotal - off + tax));
        return out.append(footer(grams)).toString();
    }

    protected abstract String header(Order order);

    protected abstract String line(String label, long cents);

    protected abstract String footer(int grams);
}
