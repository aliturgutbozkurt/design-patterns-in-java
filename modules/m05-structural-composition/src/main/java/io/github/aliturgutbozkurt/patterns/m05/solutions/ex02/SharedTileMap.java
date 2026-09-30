package io.github.aliturgutbozkurt.patterns.m05.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m05.exercises.ex02.Point;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex02.Terrain;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex02.TileMap;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex02.TileType;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex02.TileTypeRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Reference solution for assignment 02: cells hold references to shared tile types; only the position (the array
 * index) is per cell.
 *
 * @see "m05 lesson, section Flyweight"
 */
public class SharedTileMap implements TileMap {

    private final int width;
    private final int height;
    private final TileTypeRegistry registry;
    private final TileType[] cells;

    public SharedTileMap(int width, int height, TileTypeRegistry registry) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("map must be at least 1 x 1: " + width + " x " + height);
        }
        this.width = width;
        this.height = height;
        this.registry = Objects.requireNonNull(registry, "registry");
        this.cells = new TileType[width * height];
        Arrays.fill(cells, registry.typeOf(Terrain.GRASS));
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public TileType typeAt(int x, int y) {
        return cells[index(x, y)];
    }

    @Override
    public void paint(int x, int y, Terrain terrain) {
        cells[index(x, y)] = registry.typeOf(terrain);
    }

    @Override
    public int movementCost(List<Point> path) {
        int total = 0;
        for (Point point : path) {
            TileType type = typeAt(point.x(), point.y());
            if (!type.walkable()) {
                throw new IllegalArgumentException("not walkable: " + type.terrain() + " at " + point);
            }
            total += type.movementCost();
        }
        return total;
    }

    @Override
    public String render() {
        var text = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                text.append(typeAt(x, y).symbol());
            }
            text.append('\n');
        }
        return text.toString();
    }

    private int index(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("(" + x + ", " + y + ") is outside " + width + " x " + height);
        }
        return y * width + x;
    }
}
