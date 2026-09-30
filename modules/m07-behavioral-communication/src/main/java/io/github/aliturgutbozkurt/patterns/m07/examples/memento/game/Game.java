package io.github.aliturgutbozkurt.patterns.m07.examples.memento.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Originator: mutable game state that can be captured as a {@link GameState} and loaded back.
 *
 * @see "m07 lesson, section Memento — Modern Java 27"
 */
public final class Game {

    private int level = 1;
    private int health = 100;
    private Position position = new Position(0, 0);
    private final List<String> inventory = new ArrayList<>();

    public void moveTo(int x, int y) {
        position = new Position(x, y);
    }

    public void pickUp(String item) {
        inventory.add(Objects.requireNonNull(item, "item"));
    }

    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }

    public void levelUp() {
        level++;
    }

    public GameState save() {
        return new GameState(level, health, position, inventory);
    }

    public void load(GameState state) {
        Objects.requireNonNull(state, "state");
        level = state.level();
        health = state.health();
        position = state.position();
        inventory.clear();
        inventory.addAll(state.inventory());
    }

    public String status() {
        return save().describe();
    }
}
