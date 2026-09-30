package io.github.aliturgutbozkurt.patterns.m02.exercises.ex02;

import java.util.List;
import java.util.Objects;

/** GIVEN — do not modify. A generated level. */
public record Level(List<Enemy> enemies, List<Obstacle> obstacles, Reward reward) {

    public Level {
        enemies = List.copyOf(enemies);
        obstacles = List.copyOf(obstacles);
        Objects.requireNonNull(reward, "reward");
    }
}
