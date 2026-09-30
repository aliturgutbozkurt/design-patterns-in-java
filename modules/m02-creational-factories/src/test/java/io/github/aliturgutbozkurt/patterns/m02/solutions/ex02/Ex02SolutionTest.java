package io.github.aliturgutbozkurt.patterns.m02.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Biome;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Ex02Contract;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Level;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.LevelFactory;

class Ex02SolutionTest extends Ex02Contract {

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
