package io.github.aliturgutbozkurt.patterns.m05.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m05.exercises.ex01.Menu;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex01.MenuComponent;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex01.MenuItem;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex01.MenuQueries;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Reference solution for assignment 01: every query is a recursive exhaustive {@code switch} over the sealed menu.
 *
 * @see "m05 lesson, section Composite — modern Java 27"
 */
public class MenuReport implements MenuQueries {

    @Override
    public int itemCount(MenuComponent root) {
        return switch (Objects.requireNonNull(root, "root")) {
            case MenuItem _ -> 1;
            case Menu(var _, var children) -> children.stream().mapToInt(this::itemCount).sum();
        };
    }

    @Override
    public int totalCents(MenuComponent root) {
        return switch (Objects.requireNonNull(root, "root")) {
            case MenuItem(var _, var price, var _) -> price;
            case Menu(var _, var children) -> children.stream().mapToInt(this::totalCents).sum();
        };
    }

    @Override
    public List<String> itemNames(MenuComponent root) {
        return items(root).stream().map(MenuItem::name).toList();
    }

    @Override
    public List<MenuItem> vegetarian(MenuComponent root) {
        return items(root).stream().filter(MenuItem::vegetarian).toList();
    }

    private List<MenuItem> items(MenuComponent root) {
        return switch (Objects.requireNonNull(root, "root")) {
            case MenuItem item -> List.of(item);
            case Menu(var _, var children) -> children.stream().flatMap(child -> items(child).stream()).toList();
        };
    }

    @Override
    public Optional<String> pathTo(MenuComponent root, String itemName) {
        Objects.requireNonNull(root, "root");
        Objects.requireNonNull(itemName, "itemName");
        return switch (root) {
            case MenuItem item when item.name().equals(itemName) -> Optional.of(item.name());
            case MenuItem _ -> Optional.empty();
            case Menu(var name, var children) -> children.stream()
                    .map(child -> pathTo(child, itemName))
                    .flatMap(Optional::stream)
                    .findFirst()
                    .map(path -> name + " > " + path);
        };
    }

    @Override
    public String render(MenuComponent root) {
        var lines = new ArrayList<String>();
        render(Objects.requireNonNull(root, "root"), 0, lines);
        return String.join("", lines);
    }

    private void render(MenuComponent node, int level, List<String> lines) {
        String indent = "  ".repeat(level);
        switch (node) {
            case MenuItem(var name, var price, var veg) -> lines.add(indent + "- " + name + " "
                    + String.format(Locale.ROOT, "%d.%02d", price / 100, price % 100) + (veg ? " (v)" : "") + "\n");
            case Menu(var name, var children) -> {
                lines.add(indent + name + "\n");
                children.forEach(child -> render(child, level + 1, lines));
            }
        }
    }
}
