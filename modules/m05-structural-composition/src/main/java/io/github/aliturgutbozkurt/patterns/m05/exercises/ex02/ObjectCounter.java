package io.github.aliturgutbozkurt.patterns.m05.exercises.ex02;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** GIVEN — do not modify. Measures sharing by counting distinct {@link TileType} objects (by identity) on a map. */
public final class ObjectCounter {

    private ObjectCounter() {}

    public static int distinctInstances(TileMap map) {
        Set<TileType> distinct = Collections.newSetFromMap(new IdentityHashMap<>());
        for (int y = 0; y < map.height(); y++) {
            for (int x = 0; x < map.width(); x++) {
                distinct.add(map.typeAt(x, y));
            }
        }
        return distinct.size();
    }
}
