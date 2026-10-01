package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern;

import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartItem.Book;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartItem.Electronics;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.modern.CartItem.Grocery;
import java.util.List;
import java.util.Locale;

/**
 * Visitor without double dispatch: every operation is one exhaustive {@code switch} over the sealed
 * {@link CartItem}. Adding an operation is one new method; adding an item type makes every {@code switch} here
 * fail to compile until it is handled.
 *
 * @see "m08 lesson, section Visitor — Modern Java 27"
 */
public final class CartOperations {

    static final long BULKY_SURCHARGE_CENTS = 2500;

    private CartOperations() {}

    /** VAT per line: books 0 %, electronics 20 %, groceries 10 %, rounded half-up. */
    public static long tax(CartItem item) {
        return switch (item) {
            case Book _ -> 0;
            case Electronics(_, var price, _) -> percentOf(price, 20);
            case Grocery grocery -> percentOf(grocery.priceCents(), 10);
        };
    }

    public static long totalTax(List<? extends CartItem> items) {
        return items.stream().mapToLong(CartOperations::tax).sum();
    }

    /** Shipping weight in grams; a book counts as 400 g. */
    public static int shippingWeight(CartItem item) {
        return switch (item) {
            case Book _ -> 400;
            case Electronics(_, _, var grams) -> grams;
            case Grocery(_, _, var grams) -> grams;
        };
    }

    /** One aligned receipt line; electronics heavier than 20 kg get a second, surcharge line. */
    public static String receipt(CartItem item) {
        return switch (item) {
            case Electronics e when e.weightGrams() > 20_000 -> line("electronics", e.name(), e.priceCents())
                    + "\n" + line("", "bulky surcharge", BULKY_SURCHARGE_CENTS);
            case Book(var title, var price) -> line("book", title, price);
            case Electronics(var name, var price, _) -> line("electronics", name, price);
            case Grocery grocery -> line("grocery", grocery.name() + " " + grocery.grams() + " g",
                    grocery.priceCents());
        };
    }

    /** The switch matches the dynamic type, so a {@code Book} held as {@code CartItem} is described as a book. */
    public static String describe(CartItem item) {
        return switch (item) {
            case Book(var title, _) -> "the book \"" + title + "\"";
            case Electronics(var name, _, _) -> "the device " + name;
            case Grocery(var name, _, var grams) -> grams + " g of " + name;
        };
    }

    /** {@code 4500} → {@code "45.00"}. */
    public static String money(long cents) {
        return String.format(Locale.ROOT, "%d.%02d", cents / 100, cents % 100);
    }

    private static long percentOf(long cents, int percent) {
        return (Math.multiplyExact(cents, percent) + 50) / 100;
    }

    private static String line(String kind, String label, long cents) {
        return String.format(Locale.ROOT, "%-12s %-20s %10s", kind, label, money(cents));
    }
}
