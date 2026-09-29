package io.github.aliturgutbozkurt.patterns.m05.exercises.ex02;

import java.util.List;

/** Assignment 02 — your map: every cell refers to a shared {@link TileType} from the registry. */
public class SharedTileMap implements TileMap {

    public SharedTileMap(int width, int height, TileTypeRegistry registry) {
        // TODO(ex02): keep the size and the registry; fill every cell with registry.typeOf(Terrain.GRASS).
    }

    @Override
    public int width() {
        throw new UnsupportedOperationException("TODO(ex02): implement width()");
    }

    @Override
    public int height() {
        throw new UnsupportedOperationException("TODO(ex02): implement height()");
    }

    @Override
    public TileType typeAt(int x, int y) {
        throw new UnsupportedOperationException("TODO(ex02): implement typeAt(int, int)");
    }

    @Override
    public void paint(int x, int y, Terrain terrain) {
        throw new UnsupportedOperationException("TODO(ex02): implement paint(int, int, Terrain)");
    }

    @Override
    public int movementCost(List<Point> path) {
        throw new UnsupportedOperationException("TODO(ex02): implement movementCost(List<Point>)");
    }

    @Override
    public String render() {
        throw new UnsupportedOperationException("TODO(ex02): implement render()");
    }
}
