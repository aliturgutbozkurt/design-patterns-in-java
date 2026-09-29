package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.glyphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Flyweight client: stores every character as (shared glyph, row, column). The position is the
 * <em>extrinsic</em> state — it differs per occurrence, so it is kept here, not in the glyph.
 *
 * @see "m05 lesson, section Flyweight"
 */
public final class TextDocument {

    /**
     * One character on screen: a reference to a shared glyph plus where it is.
     *
     * @see "m05 lesson, section Flyweight"
     */
    public record Placement(Glyph glyph, int row, int column) {}

    private final GlyphFactory glyphs;
    private final List<Placement> placements = new ArrayList<>();
    private int row;
    private int column;

    public TextDocument(GlyphFactory glyphs) {
        this.glyphs = Objects.requireNonNull(glyphs, "glyphs");
    }

    /** Appends text at the cursor; {@code \n} starts a new row and is not a glyph. */
    public void type(String text, String font, int size) {
        for (char symbol : text.toCharArray()) {
            if (symbol == '\n') {
                row++;
                column = 0;
            } else {
                placements.add(new Placement(glyphs.glyph(symbol, font, size), row, column++));
            }
        }
    }

    public List<Placement> placements() {
        return List.copyOf(placements);
    }

    public int characterCount() {
        return placements.size();
    }

    /** Distinct glyph <em>objects</em> referenced by this document (counted by identity, not equality). */
    public int distinctGlyphs() {
        Set<Glyph> distinct = Collections.newSetFromMap(new IdentityHashMap<>());
        placements.forEach(placement -> distinct.add(placement.glyph()));
        return distinct.size();
    }

    /** The plain text again, rows separated by {@code \n}. */
    public String text() {
        var text = new StringBuilder();
        int currentRow = 0;
        for (Placement placement : placements) {
            for (; currentRow < placement.row(); currentRow++) {
                text.append('\n');
            }
            text.append(placement.glyph().symbol());
        }
        return text.toString();
    }
}
