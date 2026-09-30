package io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry;

import java.util.Objects;

/**
 * A unit with extra mutable state (arrows) that its copy must not share.
 *
 * @see "m03 lesson, section Prototype"
 */
public final class Archer implements Unit {

    private final Stats stats;
    private Position position = new Position(0, 0);
    private int arrows;

    public Archer(Stats stats, int arrows) {
        this.stats = Objects.requireNonNull(stats, "stats");
        if (arrows < 0) {
            throw new IllegalArgumentException("arrows must not be negative: " + arrows);
        }
        this.arrows = arrows;
    }

    private Archer(Archer other) {
        this.stats = other.stats;
        this.position = other.position;
        this.arrows = other.arrows;
    }

    @Override
    public String type() {
        return "archer";
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

    public int arrows() {
        return arrows;
    }

    public void shoot() {
        if (arrows == 0) {
            throw new IllegalStateException("no arrows left");
        }
        arrows--;
    }

    @Override
    public Archer copy() {
        return new Archer(this);
    }

    @Override
    public String describe() {
        return Unit.super.describe() + " arrows=" + arrows;
    }
}
