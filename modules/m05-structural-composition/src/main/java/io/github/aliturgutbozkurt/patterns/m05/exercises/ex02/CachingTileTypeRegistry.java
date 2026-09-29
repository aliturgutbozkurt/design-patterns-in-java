package io.github.aliturgutbozkurt.patterns.m05.exercises.ex02;

/** Assignment 02 — your flyweight factory. */
public class CachingTileTypeRegistry implements TileTypeRegistry {

    @Override
    public TileType typeOf(Terrain terrain) {
        // TODO(ex02): return a shared instance; create it (TileType.of) at most once per terrain, thread-safely.
        throw new UnsupportedOperationException("TODO(ex02): implement typeOf(Terrain)");
    }

    @Override
    public int createdCount() {
        throw new UnsupportedOperationException("TODO(ex02): implement createdCount()");
    }
}
