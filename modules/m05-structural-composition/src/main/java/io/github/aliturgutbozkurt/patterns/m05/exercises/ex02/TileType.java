package io.github.aliturgutbozkurt.patterns.m05.exercises.ex02;

import java.util.List;
import java.util.Objects;

/**
 * GIVEN — do not modify. Intrinsic tile data. The 8 × 8 sprite makes every instance "heavy" — exactly the kind of
 * object worth sharing. Build one with {@link #of(Terrain)}.
 */
public record TileType(Terrain terrain, char symbol, int movementCost, boolean walkable, List<String> sprite) {

    public TileType {
        Objects.requireNonNull(terrain, "terrain");
        sprite = List.copyOf(sprite);
    }

    /** A new, fully built tile type for {@code terrain} — a new object on every call. */
    public static TileType of(Terrain terrain) {
        return new TileType(terrain, terrain.symbol(), terrain.movementCost(), terrain.walkable(),
                Sprites.forTerrain(terrain));
    }
}
