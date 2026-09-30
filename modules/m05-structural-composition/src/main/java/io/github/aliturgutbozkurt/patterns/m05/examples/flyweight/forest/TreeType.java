package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest;

/**
 * Flyweight: what all trees of one kind have in common (in a real game: mesh, texture bitmap, colour data).
 * Immutable, so any number of trees can point at the same instance.
 *
 * @see "m05 lesson, section Flyweight"
 */
public record TreeType(String species, String colour, String texture) {

    public TreeType {
        if (species == null || species.isBlank() || colour == null || texture == null) {
            throw new IllegalArgumentException("species, colour and texture are required");
        }
    }
}
