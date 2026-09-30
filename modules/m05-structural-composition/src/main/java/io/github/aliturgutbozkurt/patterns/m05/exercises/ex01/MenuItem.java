package io.github.aliturgutbozkurt.patterns.m05.exercises.ex01;

/** GIVEN — do not modify. A dish or drink; the price is in cents, so 450 means 4.50. */
public record MenuItem(String name, int priceCents, boolean vegetarian) implements MenuComponent {

    public MenuItem {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (priceCents < 0) {
            throw new IllegalArgumentException("price must not be negative: " + priceCents);
        }
    }
}
