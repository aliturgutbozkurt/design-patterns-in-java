package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic;

import java.util.Locale;

/**
 * Concrete visitor: one aligned receipt line per item.
 *
 * @see "m08 lesson, section Visitor — Classic Java"
 */
public final class ReceiptVisitor implements CartVisitor<String> {

    @Override
    public String visitBook(Book book) {
        return line("book", book.title(), book.priceCents());
    }

    @Override
    public String visitElectronics(Electronics electronics) {
        return line("electronics", electronics.name(), electronics.priceCents());
    }

    @Override
    public String visitGrocery(Grocery grocery) {
        return line("grocery", grocery.name() + " " + grocery.grams() + " g", grocery.priceCents());
    }

    /** {@code 4500} → {@code "45.00"}. */
    public static String money(long cents) {
        return String.format(Locale.ROOT, "%d.%02d", cents / 100, cents % 100);
    }

    private static String line(String kind, String label, long cents) {
        return String.format(Locale.ROOT, "%-12s %-20s %10s", kind, label, money(cents));
    }
}
