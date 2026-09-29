package io.github.aliturgutbozkurt.patterns.m05.exercises.ex02;

/**
 * GIVEN — do not modify. The terrain table: map symbol, movement cost and whether units can walk on it. Water and
 * mountains are not walkable, so their cost is never used (0).
 */
public enum Terrain {
    GRASS('.', 1, true),
    SAND(':', 2, true),
    FOREST('T', 3, true),
    WATER('~', 0, false),
    MOUNTAIN('^', 0, false);

    private final char symbol;
    private final int movementCost;
    private final boolean walkable;

    Terrain(char symbol, int movementCost, boolean walkable) {
        this.symbol = symbol;
        this.movementCost = movementCost;
        this.walkable = walkable;
    }

    public char symbol() {
        return symbol;
    }

    public int movementCost() {
        return movementCost;
    }

    public boolean walkable() {
        return walkable;
    }
}
