package io.github.aliturgutbozkurt.patterns.m05.exercises.ex01;

import java.util.List;
import java.util.Optional;

/**
 * GIVEN — do not modify. Questions about a menu tree. Traversal is depth-first in menu order; a single
 * {@link MenuItem} is a valid tree; every method throws {@link NullPointerException} for a {@code null} argument.
 */
public interface MenuQueries {

    /** Number of items (menus are not counted). */
    int itemCount(MenuComponent root);

    /** Sum of all item prices, in cents. */
    int totalCents(MenuComponent root);

    /** Item names, depth-first in menu order. */
    List<String> itemNames(MenuComponent root);

    /** Vegetarian items, depth-first in menu order. */
    List<MenuItem> vegetarian(MenuComponent root);

    /** Names from the root down to the first item called {@code itemName}, joined with {@code " > "}. */
    Optional<String> pathTo(MenuComponent root, String itemName);

    /**
     * One line per node, two spaces of indent per level: a menu as its name, an item as
     * {@code - <name> <euros>.<cents>} followed by {@code " (v)"} when vegetarian. Every line ends with {@code \n}.
     */
    String render(MenuComponent root);
}
