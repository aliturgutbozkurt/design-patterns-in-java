package io.github.aliturgutbozkurt.patterns.m05.exercises.ex02;

import java.util.ArrayList;
import java.util.List;

/** GIVEN — do not modify. Draws an 8 × 8 text sprite for a terrain (a stand-in for real image data). */
public final class Sprites {

    public static final int SIZE = 8;

    private Sprites() {}

    public static List<String> forTerrain(Terrain terrain) {
        var rows = new ArrayList<String>();
        for (int row = 0; row < SIZE; row++) {
            var line = new StringBuilder();
            for (int column = 0; column < SIZE; column++) {
                line.append((row + column) % 2 == 0 ? terrain.symbol() : ' ');
            }
            rows.add(line.toString());
        }
        return List.copyOf(rows);
    }
}
