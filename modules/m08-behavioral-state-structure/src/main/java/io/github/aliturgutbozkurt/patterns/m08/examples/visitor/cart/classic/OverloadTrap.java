package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic;

/**
 * Why Visitor needs {@code accept}: Java picks an overload at compile time from the <em>static</em> type of the
 * argument. A {@code Book} held in a {@code CartItem} variable therefore reaches {@code describe(CartItem)}, while
 * {@code accept} dispatches on the dynamic type and reaches {@code visitBook}.
 *
 * @see "m08 lesson, section Visitor — Classic Java"
 */
public final class OverloadTrap {

    private OverloadTrap() {}

    public static String describe(CartItem item) {
        return "a cart item";
    }

    public static String describe(Book book) {
        return "the book \"" + book.title() + "\"";
    }

    /** Double dispatch: {@code accept} (dynamic type of the item), then {@code visitX} (the visitor's method). */
    public static String describeWithVisitor(CartItem item) {
        return item.accept(new CartVisitor<>() {
            @Override
            public String visitBook(Book book) {
                return describe(book);
            }

            @Override
            public String visitElectronics(Electronics electronics) {
                return "the device " + electronics.name();
            }

            @Override
            public String visitGrocery(Grocery grocery) {
                return grocery.grams() + " g of " + grocery.name();
            }
        });
    }
}
