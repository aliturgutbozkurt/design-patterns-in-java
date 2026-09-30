package io.github.aliturgutbozkurt.patterns.m05.exercises.ex02;

import java.util.List;

/**
 * GIVEN — do not modify. The "before" picture: correct, but every cell holds its own new {@link TileType}, so a
 * 256 × 256 map carries 65 536 heavy objects. Use it as a behaviour reference, not as a model for sharing.
 */
public final class NaiveTileMap implements TileMap {

    private final int width;
    private final int height;
    private final TileType[] cells;

    public NaiveTileMap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("map must be at least 1 x 1: " + width + " x " + height);
        }
        this.width = width;
        this.height = height;
        this.cells = new TileType[width * height];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = TileType.of(Terrain.GRASS);   // a new object per cell
        }
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
        cells[index(x, y)] = TileType.of(terrain);   // a new object per paint
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
