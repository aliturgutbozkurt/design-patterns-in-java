package io.github.aliturgutbozkurt.patterns.m05.exercises.ex01;

import java.util.HashSet;
import java.util.List;

/** GIVEN — do not modify. A menu or submenu; its children are an immutable copy and have distinct names. */
public record Menu(String name, List<MenuComponent> children) implements MenuComponent {

    public Menu {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        children = List.copyOf(children);
        var names = new HashSet<String>();
        for (MenuComponent child : children) {
            if (!names.add(child.name())) {
                throw new IllegalArgumentException("duplicate name in " + name + ": " + child.name());
            }
        }
    }

    public static Menu of(String name, MenuComponent... children) {
        return new Menu(name, List.of(children));
    }
}
