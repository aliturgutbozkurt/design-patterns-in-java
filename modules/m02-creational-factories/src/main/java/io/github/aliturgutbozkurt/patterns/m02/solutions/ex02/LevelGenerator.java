package io.github.aliturgutbozkurt.patterns.m02.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.Level;
import io.github.aliturgutbozkurt.patterns.m02.exercises.ex02.LevelFactory;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Reference solution for assignment 02: every product comes from the one factory it was given.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public class LevelGenerator {

    private final LevelFactory factory;

    public LevelGenerator(LevelFactory factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public Level generate(int difficulty) {
        if (difficulty < 1 || difficulty > 10) {
            throw new IllegalArgumentException("difficulty must be in 1..10: " + difficulty);
        }
        var enemies = Stream.generate(factory::enemy).limit(difficulty).toList();
        var obstacles = Stream.generate(factory::obstacle).limit(difficulty).toList();
        return new Level(enemies, obstacles, factory.reward());
    }
}
