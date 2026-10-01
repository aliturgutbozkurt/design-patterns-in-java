package io.github.aliturgutbozkurt.patterns.m02.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Biome;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Enemy;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.LevelFactory;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Obstacle;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Reward;

/**
 * Reference solution for assignment 02: the forest family.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public class ForestLevelFactory implements LevelFactory {

    @Override
    public Enemy enemy() {
        return new Products.SimpleEnemy("Wolf", 30, Biome.FOREST);
    }

    @Override
    public Obstacle obstacle() {
        return new Products.SimpleObstacle("Fallen log", 5, Biome.FOREST);
    }

    @Override
    public Reward reward() {
        return new Products.SimpleReward("Mushroom", 10, Biome.FOREST);
    }
}
