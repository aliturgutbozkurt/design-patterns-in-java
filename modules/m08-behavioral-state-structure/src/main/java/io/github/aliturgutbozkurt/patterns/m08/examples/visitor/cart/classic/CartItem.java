package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic;

/**
 * Visitor element: every cart item accepts a visitor and calls the {@code visitX} method for its own type. That call
 * is the second dispatch, and it is what makes the dynamic type of the item reach the visitor.
 *
 * @see "m08 lesson, section Visitor — Classic Java"
 */
public interface CartItem {

    <R> R accept(CartVisitor<R> visitor);
}
