package io.github.aliturgutbozkurt.patterns.m02.exercises.ex02;

import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m02-creational-factories test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected LevelFactory forestFactory() {
        return new ForestLevelFactory();
    }

    @Override
    protected LevelFactory desertFactory() {
        return new DesertLevelFactory();
    }

    @Override
    protected Level generate(LevelFactory factory, int difficulty) {
        return new LevelGenerator(factory).generate(difficulty);
    }

    @Override
    protected LevelFactory forBiome(Biome biome) {
        return LevelFactories.forBiome(biome);
    }

    @Override
    protected LevelFactory forName(String name) {
        return LevelFactories.forName(name);
    }
}
