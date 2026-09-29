package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Flyweight client: a forest of positioned trees. Where the {@link TreeType}s come from is pluggable, so the same
 * class shows the shared version ({@code factory::typeOf}) and the naive one ({@code TreeType::new}).
 *
 * @see "m05 lesson, section Flyweight"
 */
public final class Forest {

    /**
     * Supplies the tree type for a planting: a flyweight factory, or a plain constructor for comparison.
     *
     * @see "m05 lesson, section Flyweight"
     */
    @FunctionalInterface
    public interface TypeSource {
        TreeType typeOf(String species, String colour, String texture);
    }

    private record Kind(String species, String colour, String texture) {}

    private static final List<Kind> KINDS = List.of(
            new Kind("oak", "green", "rough bark"),
            new Kind("pine", "dark green", "needles"),
            new Kind("birch", "white", "smooth bark"));

    private final TypeSource types;
    private final List<Tree> trees = new ArrayList<>();

    public Forest(TypeSource types) {
        this.types = Objects.requireNonNull(types, "types");
    }

    public void plant(int x, int y, String species, String colour, String texture) {
        trees.add(new Tree(x, y, types.typeOf(species, colour, texture)));
    }

    /** Plants a tree on every cell of a columns × rows grid; the kind follows a fixed pattern, (x + 2y) mod 3. */
    public void plantGrid(int columns, int rows) {
        if (columns <= 0 || rows <= 0) {
            throw new IllegalArgumentException("grid must be at least 1 x 1: " + columns + " x " + rows);
        }
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                Kind kind = KINDS.get((x + 2 * y) % KINDS.size());
                plant(x, y, kind.species(), kind.colour(), kind.texture());
            }
        }
    }

    public int treeCount() {
        return trees.size();
    }

    /** Distinct {@link TreeType} <em>objects</em> in the forest, counted by identity (not by {@code equals}). */
    public int distinctTypeInstances() {
        Set<TreeType> distinct = Collections.newSetFromMap(new IdentityHashMap<>());
        trees.forEach(tree -> distinct.add(tree.type()));
        return distinct.size();
    }

    /** Trees inside the rectangle (inclusive), row by row, left to right. */
    public List<Tree> region(int minX, int minY, int maxX, int maxY) {
        return trees.stream()
                .filter(t -> t.x() >= minX && t.x() <= maxX && t.y() >= minY && t.y() <= maxY)
                .sorted(Comparator.comparingInt(Tree::y).thenComparingInt(Tree::x))
                .toList();
    }
}
