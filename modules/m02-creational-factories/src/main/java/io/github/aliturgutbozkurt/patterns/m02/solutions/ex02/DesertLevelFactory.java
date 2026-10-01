package io.github.aliturgutbozkurt.patterns.m02.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Biome;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Enemy;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.LevelFactory;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Obstacle;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Reward;

/**
 * Reference solution for assignment 02: the desert family.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public class DesertLevelFactory implements LevelFactory {

    @Override
    public Enemy enemy() {
        return new Products.SimpleEnemy("Scorpion", 20, Biome.DESERT);
    }

    @Override
    public Obstacle obstacle() {
        return new Products.SimpleObstacle("Quicksand", 12, Biome.DESERT);
    }

    @Override
    public Reward reward() {
        return new Products.SimpleReward("Water flask", 15, Biome.DESERT);
    }
}
