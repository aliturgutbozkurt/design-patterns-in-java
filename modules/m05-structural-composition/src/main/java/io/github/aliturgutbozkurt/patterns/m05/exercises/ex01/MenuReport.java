package io.github.aliturgutbozkurt.patterns.m05.exercises.ex01;

import java.util.List;
import java.util.Optional;

/** Assignment 01 — your menu queries. Write each one as a recursive {@code switch} over {@link MenuComponent}. */
public class MenuReport implements MenuQueries {

    @Override
    public int itemCount(MenuComponent root) {
        // TODO(ex01): an item counts 1, a menu the sum over its children.
        throw new UnsupportedOperationException("TODO(ex01): implement itemCount(MenuComponent)");
    }

    @Override
    public int totalCents(MenuComponent root) {
        throw new UnsupportedOperationException("TODO(ex01): implement totalCents(MenuComponent)");
    }

    @Override
    public List<String> itemNames(MenuComponent root) {
        throw new UnsupportedOperationException("TODO(ex01): implement itemNames(MenuComponent)");
    }

    @Override
    public List<MenuItem> vegetarian(MenuComponent root) {
        throw new UnsupportedOperationException("TODO(ex01): implement vegetarian(MenuComponent)");
    }

    @Override
    public Optional<String> pathTo(MenuComponent root, String itemName) {
        // TODO(ex01): carry the path of names down the recursion.
        throw new UnsupportedOperationException("TODO(ex01): implement pathTo(MenuComponent, String)");
    }

    @Override
    public String render(MenuComponent root) {
        throw new UnsupportedOperationException("TODO(ex01): implement render(MenuComponent)");
    }
}
