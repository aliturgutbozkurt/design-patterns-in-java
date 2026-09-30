package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.cart.classic;

import java.util.Objects;

/**
 * Concrete element: a book. {@code accept} calls {@code visitBook(this)}, where {@code this} has the static type
 * {@code Book}.
 *
 * @see "m08 lesson, section Visitor — Classic Java"
 */
public record Book(String title, long priceCents) implements CartItem {

    public Book {
        Objects.requireNonNull(title, "title");
        if (priceCents < 0) {
            throw new IllegalArgumentException("price must not be negative: " + priceCents);
        }
    }

    @Override
    public <R> R accept(CartVisitor<R> visitor) {
        return visitor.visitBook(this);
    }
}
