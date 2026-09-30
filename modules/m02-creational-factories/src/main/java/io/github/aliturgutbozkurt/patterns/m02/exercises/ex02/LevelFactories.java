package io.github.aliturgutbozkurt.patterns.m02.exercises.ex02;

/** Assignment 02 — static factories that choose the family. */
public final class LevelFactories {

    private LevelFactories() {}

    public static LevelFactory forBiome(Biome biome) {
        // TODO(ex02): an exhaustive switch over Biome.
        throw new UnsupportedOperationException("TODO(ex02): implement forBiome(Biome)");
    }

    /** Case-insensitive biome name, e.g. {@code "forest"}; unknown names throw {@code IllegalArgumentException}. */
    public static LevelFactory forName(String name) {
        // TODO(ex02)
        throw new UnsupportedOperationException("TODO(ex02): implement forName(String)");
    }
}
