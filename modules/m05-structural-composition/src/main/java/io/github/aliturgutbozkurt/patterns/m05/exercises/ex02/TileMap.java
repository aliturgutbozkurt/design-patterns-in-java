package io.github.aliturgutbozkurt.patterns.m05.exercises.ex02;

import java.util.List;

/**
 * GIVEN — do not modify. A rectangular map of tiles; a new map is all {@link Terrain#GRASS}. Coordinates outside the
 * map throw {@link IndexOutOfBoundsException}.
 */
public interface TileMap {

    int width();

    int height();

    TileType typeAt(int x, int y);

    void paint(int x, int y, Terrain terrain);

    /**
     * Sum of the movement costs of every tile on {@code path}.
     *
     * @throws IllegalArgumentException if a tile on the path is not walkable
     */
    int movementCost(List<Point> path);

    /** One line of symbols per row, top row first; every line ends with {@code \n}. */
    String render();
}
