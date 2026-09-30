package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic;

/**
 * Visitor: one method per concrete element, with a generic result type so an operation can return a value instead
 * of accumulating it in a field.
 *
 * @param <R> what the operation computes for one item
 * @see "m08 lesson, section Visitor — Classic Java"
 */
public interface CartVisitor<R> {

    R visitBook(Book book);

    R visitElectronics(Electronics electronics);

    R visitGrocery(Grocery grocery);
}
