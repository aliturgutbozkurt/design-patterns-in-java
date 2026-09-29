package io.github.aliturgutbozkurt.patterns.m05.exercises.ex02;

/**
 * GIVEN — do not modify. The flyweight factory: one shared {@link TileType} per terrain, safe to call from many
 * threads, creating each type at most once.
 */
public interface TileTypeRegistry {

    /** The shared tile type for {@code terrain}; the same instance on every call. */
    TileType typeOf(Terrain terrain);

    /** How many {@link TileType} objects this registry has created so far. */
    int createdCount();
}
