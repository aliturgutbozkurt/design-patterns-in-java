package io.github.aliturgutbozkurt.patterns.m05.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m05.exercises.ex02.Ex02Contract;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex02.TileMap;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex02.TileTypeRegistry;

class Ex02SolutionTest extends Ex02Contract {

    @Override
    protected TileTypeRegistry newRegistry() {
        return new CachingTileTypeRegistry();
    }

    @Override
    protected TileMap newMap(int width, int height, TileTypeRegistry registry) {
        return new SharedTileMap(width, height, registry);
    }
}
