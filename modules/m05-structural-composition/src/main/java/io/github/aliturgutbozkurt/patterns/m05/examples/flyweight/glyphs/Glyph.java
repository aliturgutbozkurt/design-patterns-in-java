package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.glyphs;

/**
 * Flyweight: the <em>intrinsic</em> state of a character on screen — the same for every occurrence, so one immutable
 * instance can be shared by all of them. Where it appears (row, column) is extrinsic and lives elsewhere.
 *
 * @see "m05 lesson, section Flyweight"
 */
public record Glyph(char symbol, String font, int size) {

    public Glyph {
        if (font == null || font.isBlank()) {
            throw new IllegalArgumentException("font must not be blank");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive: " + size);
        }
    }
}
