package io.github.aliturgutbozkurt.patterns.m05.exercises.ex02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

/** Assignment 02 — Flyweight map tiles. Each test is one acceptance criterion of the brief. */
public abstract class Ex02Contract {

    protected abstract TileTypeRegistry newRegistry();

    protected abstract TileMap newMap(int width, int height, TileTypeRegistry registry);

    private TileMap newMap(int width, int height) {
        return newMap(width, height, newRegistry());
    }

    /** Stripes of water and forest over grass — the same picture on any map. */
    private static void paintLandscape(TileMap map) {
        for (int y = 0; y < map.height(); y++) {
            for (int x = 0; x < map.width(); x++) {
                if (y % 4 == 0) {
                    map.paint(x, y, Terrain.WATER);
                } else if (x % 3 == 0) {
                    map.paint(x, y, Terrain.FOREST);
                }
            }
        }
    }

    @Test
    void registryReturnsTheSameInstanceForTheSameTerrain() {
        TileTypeRegistry registry = newRegistry();
        TileType grass = registry.typeOf(Terrain.GRASS);
        assertThat(registry.typeOf(Terrain.GRASS)).isSameAs(grass);
        assertThat(registry.typeOf(Terrain.SAND)).isNotSameAs(grass);
        assertThat(grass).isEqualTo(TileType.of(Terrain.GRASS));
    }

    @Test
    void registryCreatesEachTypeAtMostOnce() {
        TileTypeRegistry registry = newRegistry();
        assertThat(registry.createdCount()).isZero();
        for (int round = 0; round < 10; round++) {
            for (Terrain terrain : Terrain.values()) {
                registry.typeOf(terrain);
            }
        }
        assertThat(registry.createdCount()).isEqualTo(Terrain.values().length);
    }

    @Test
    void registryIsSafeUnderManyVirtualThreads() throws Exception {
        TileTypeRegistry registry = newRegistry();
        Terrain[] terrains = Terrain.values();
        List<Future<TileType>> results = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1000; i++) {
                Terrain terrain = terrains[i % terrains.length];
                results.add(executor.submit(() -> registry.typeOf(terrain)));
            }
        }
        Set<TileType> distinct = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Future<TileType> result : results) {
            distinct.add(result.get());
        }
        assertThat(distinct).hasSize(terrains.length);
        assertThat(registry.createdCount()).isEqualTo(terrains.length);
    }

    @Test
    void newMapIsAllGrass() {
        TileMap map = newMap(4, 3);
        assertThat(map.width()).isEqualTo(4);
        assertThat(map.height()).isEqualTo(3);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 4; x++) {
                assertThat(map.typeAt(x, y).terrain()).isEqualTo(Terrain.GRASS);
            }
        }
    }

    @Test
    void paintChangesOnlyOneCell() {
        TileMap map = newMap(3, 3);
        map.paint(1, 2, Terrain.MOUNTAIN);
        assertThat(map.typeAt(1, 2).terrain()).isEqualTo(Terrain.MOUNTAIN);
        assertThat(map.typeAt(2, 1).terrain()).isEqualTo(Terrain.GRASS);
        assertThat(map.typeAt(0, 2).terrain()).isEqualTo(Terrain.GRASS);
        assertThat(map.typeAt(1, 1).terrain()).isEqualTo(Terrain.GRASS);
    }

    @Test
    void sharedMapHoldsOneInstancePerTerrainUsed() {
        TileMap naive = new NaiveTileMap(256, 256);
        paintLandscape(naive);
        TileMap shared = newMap(256, 256);
        paintLandscape(shared);

        assertThat(ObjectCounter.distinctInstances(naive)).isEqualTo(65_536);
        assertThat(ObjectCounter.distinctInstances(shared)).isEqualTo(3);
        assertThat(shared.render()).isEqualTo(naive.render());
    }

    @Test
    void mapsShareTypesThroughTheRegistry() {
        TileTypeRegistry registry = newRegistry();
        TileMap first = newMap(5, 5, registry);
        TileMap second = newMap(7, 2, registry);
        first.paint(0, 0, Terrain.SAND);
        second.paint(6, 1, Terrain.SAND);
        assertThat(first.typeAt(0, 0)).isSameAs(second.typeAt(6, 1)).isSameAs(registry.typeOf(Terrain.SAND));
        assertThat(first.typeAt(4, 4)).isSameAs(second.typeAt(0, 0));
        assertThat(registry.createdCount()).isEqualTo(2);
    }

    @Test
    void movementCostSumsThePath() {
        TileMap map = newMap(4, 1);
        map.paint(1, 0, Terrain.SAND);
        map.paint(2, 0, Terrain.FOREST);
        List<Point> path = List.of(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0));
        assertThat(map.movementCost(path)).isEqualTo(1 + 2 + 3 + 1);
        assertThat(map.movementCost(List.of())).isZero();
    }

    @Test
    void pathThroughWaterIsRejected() {
        TileMap map = newMap(3, 1);
        map.paint(1, 0, Terrain.WATER);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> map.movementCost(List.of(new Point(0, 0), new Point(1, 0), new Point(2, 0))));
        map.paint(1, 0, Terrain.MOUNTAIN);
        assertThatIllegalArgumentException().isThrownBy(() -> map.movementCost(List.of(new Point(1, 0))));
    }

    @Test
    void rendersRowsOfSymbols() {
        TileMap map = newMap(4, 3);
        map.paint(0, 0, Terrain.WATER);
        map.paint(1, 0, Terrain.WATER);
        map.paint(2, 1, Terrain.FOREST);
        map.paint(3, 2, Terrain.MOUNTAIN);
        map.paint(0, 2, Terrain.SAND);
        assertThat(map.render()).isEqualTo("""
                ~~..
                ..T.
                :..^
                """);
    }

    @Test
    void rejectsCoordinatesOutsideTheMap() {
        TileMap map = newMap(3, 2);
        assertThatThrownBy(() -> map.typeAt(-1, 0)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> map.typeAt(0, 2)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> map.paint(3, 0, Terrain.SAND)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> map.movementCost(List.of(new Point(5, 5))))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }
}
