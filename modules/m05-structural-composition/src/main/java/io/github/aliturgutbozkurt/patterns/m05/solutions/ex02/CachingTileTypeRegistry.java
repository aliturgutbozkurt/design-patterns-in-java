package io.github.aliturgutbozkurt.patterns.m05.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m05.exercises.ex02.Terrain;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex02.TileType;
import io.github.aliturgutbozkurt.patterns.m05.exercises.ex02.TileTypeRegistry;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Reference solution for assignment 02: {@code computeIfAbsent} creates each tile type at most once, even when many
 * threads ask at the same moment.
 *
 * @see "m05 lesson, section Flyweight"
 */
public class CachingTileTypeRegistry implements TileTypeRegistry {

    private final Map<Terrain, TileType> types = new ConcurrentHashMap<>();
    private final AtomicInteger created = new AtomicInteger();

    @Override
    public TileType typeOf(Terrain terrain) {
        Objects.requireNonNull(terrain, "terrain");
        return types.computeIfAbsent(terrain, key -> {
            TileType type = TileType.of(key);
            created.incrementAndGet();
            return type;
        });
    }

    @Override
    public int createdCount() {
        return created.get();
    }
}
