package io.github.aliturgutbozkurt.patterns.m02.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Biome;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.LevelFactory;
import java.util.Arrays;
import java.util.Locale;

/** Reference solution for assignment 02: static factories that choose the family. */
public final class LevelFactories {

    private LevelFactories() {}

    public static LevelFactory forBiome(Biome biome) {
        return switch (biome) {
            case FOREST -> new ForestLevelFactory();
            case DESERT -> new DesertLevelFactory();
        };
    }

    public static LevelFactory forName(String name) {
        Biome biome = Arrays.stream(Biome.values())
                .filter(candidate -> candidate.name().equals(name.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "unknown biome: " + name + " (known: " + Arrays.toString(Biome.values()) + ")"));
        return forBiome(biome);
    }
}
