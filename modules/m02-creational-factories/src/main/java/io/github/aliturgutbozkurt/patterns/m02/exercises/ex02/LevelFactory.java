package io.github.aliturgutbozkurt.patterns.m02.exercises.ex02;

/** GIVEN — do not modify. Abstract Factory: creates the products of one biome. */
public interface LevelFactory {

    Enemy enemy();

    Obstacle obstacle();

    Reward reward();
}
