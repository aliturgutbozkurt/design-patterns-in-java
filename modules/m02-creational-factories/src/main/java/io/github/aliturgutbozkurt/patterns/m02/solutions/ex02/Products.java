package io.github.aliturgutbozkurt.patterns.m02.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Biome;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Enemy;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Obstacle;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Reward;

/** Reference solution for assignment 02: simple record products; record accessors implement the interfaces. */
final class Products {

    private Products() {}

    record SimpleEnemy(String name, int hitPoints, Biome biome) implements Enemy {}

    record SimpleObstacle(String name, int damage, Biome biome) implements Obstacle {}

    record SimpleReward(String name, int points, Biome biome) implements Reward {}
}
