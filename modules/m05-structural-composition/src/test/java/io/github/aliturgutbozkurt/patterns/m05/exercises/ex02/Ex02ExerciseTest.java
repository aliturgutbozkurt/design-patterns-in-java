package io.github.aliturgutbozkurt.patterns.m05.exercises.ex02;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m05-structural-composition test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected TileTypeRegistry newRegistry() {
        return new CachingTileTypeRegistry();
    }

    @Override
    protected TileMap newMap(int width, int height, TileTypeRegistry registry) {
        return new SharedTileMap(width, height, registry);
    }
}
