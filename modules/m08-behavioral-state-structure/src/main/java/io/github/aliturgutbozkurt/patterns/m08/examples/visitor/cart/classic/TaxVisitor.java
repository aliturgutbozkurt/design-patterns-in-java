package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic;

/**
 * Concrete visitor: VAT per line (books 0 %, electronics 20 %, groceries 10 %), rounded half-up to the cent.
 *
 * @see "m08 lesson, section Visitor — Classic Java"
 */
public final class TaxVisitor implements CartVisitor<Long> {

    @Override
    public Long visitBook(Book book) {
        return 0L;
    }

    @Override
    public Long visitElectronics(Electronics electronics) {
        return percentOf(electronics.priceCents(), 20);
    }

    @Override
    public Long visitGrocery(Grocery grocery) {
        return percentOf(grocery.priceCents(), 10);
    }

    private static long percentOf(long cents, int percent) {
        return (Math.multiplyExact(cents, percent) + 50) / 100;
    }
}
