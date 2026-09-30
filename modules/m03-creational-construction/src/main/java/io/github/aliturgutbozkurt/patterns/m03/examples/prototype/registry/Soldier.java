package io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry;

import java.util.Objects;

/**
 * A mutable unit (its position changes) copied with a copy constructor.
 *
 * @see "m03 lesson, section Prototype"
 */
public final class Soldier implements Unit {

    private final Stats stats;
    private Position position = new Position(0, 0);

    public Soldier(Stats stats) {
        this.stats = Objects.requireNonNull(stats, "stats");
    }

    private Soldier(Soldier other) {
        this.stats = other.stats;          // immutable: shared
        this.position = other.position;    // immutable value: shared; the field itself is per unit
    }

    @Override
    public String type() {
        return "soldier";
    }

    @Override
    public Stats stats() {
        return stats;
    }

    @Override
    public Position position() {
        return position;
    }

    @Override
    public void moveTo(int x, int y) {
        position = new Position(x, y);
    }

    @Override
    public Soldier copy() {
        return new Soldier(this);
    }
}
