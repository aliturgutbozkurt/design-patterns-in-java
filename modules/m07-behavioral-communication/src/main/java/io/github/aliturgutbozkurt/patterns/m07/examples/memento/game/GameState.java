package io.github.aliturgutbozkurt.patterns.m07.examples.memento.game;

import java.util.List;
import java.util.Objects;

/**
 * A deeply immutable snapshot of a whole game: {@code List.copyOf} in the compact constructor means later changes
 * to the game's inventory can never leak into a saved state.
 *
 * @see "m07 lesson, section Memento — Modern Java 27"
 */
public record GameState(int level, int health, Position position, List<String> inventory) {

    public GameState {
        if (level < 1) {
            throw new IllegalArgumentException("level must be at least 1: " + level);
        }
        if (health < 0 || health > 100) {
            throw new IllegalArgumentException("health must be 0..100: " + health);
        }
        Objects.requireNonNull(position, "position");
        inventory = List.copyOf(inventory);
    }

    /** E.g. {@code "level 2, health 70, at (3,4), inventory [sword, key]"}. */
    public String describe() {
        return "level " + level + ", health " + health + ", at (" + position.x() + "," + position.y() + "), inventory "
                + inventory;
    }
}
