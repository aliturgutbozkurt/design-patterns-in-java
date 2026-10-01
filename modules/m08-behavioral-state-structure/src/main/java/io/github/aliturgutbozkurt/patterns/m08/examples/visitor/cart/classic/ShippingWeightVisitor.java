package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic;

/**
 * Concrete visitor: shipping weight in grams; a book counts as 400 g.
 *
 * @see "m08 lesson, section Visitor — Classic Java"
 */
public final class ShippingWeightVisitor implements CartVisitor<Integer> {

    static final int BOOK_GRAMS = 400;

    @Override
    public Integer visitBook(Book book) {
        return BOOK_GRAMS;
    }

    @Override
    public Integer visitElectronics(Electronics electronics) {
        return electronics.weightGrams();
    }

    @Override
    public Integer visitGrocery(Grocery grocery) {
        return grocery.grams();
    }
}
