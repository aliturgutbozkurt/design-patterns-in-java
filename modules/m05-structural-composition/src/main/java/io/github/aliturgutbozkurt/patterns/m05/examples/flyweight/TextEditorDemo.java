package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight;

import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.glyphs.GlyphFactory;
import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.glyphs.TextDocument;

/** Run: {@code java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/flyweight/TextEditorDemo.java} */
public final class TextEditorDemo {

    private TextEditorDemo() {}

    public static void main(String[] args) {
        var glyphs = new GlyphFactory();
        var document = new TextDocument(glyphs);
        String line = "the quick brown fox jumps over the lazy dog";

        document.type(line, "Serif", 12);
        report("one line", document, glyphs);
        document.type("\n" + line, "Serif", 12);
        report("same line again", document, glyphs);
        document.type("\nHello", "Sans", 18);
        report("heading \"Hello\" in Sans 18", document, glyphs);

        System.out.println("'e' Serif 12 twice -> same instance: "
                + (glyphs.glyph('e', "Serif", 12) == glyphs.glyph('e', "Serif", 12)));
        System.out.println("'e' Serif 12 vs Sans 18 -> same instance: "
                + (glyphs.glyph('e', "Serif", 12) == glyphs.glyph('e', "Sans", 18)));
    }

    private static void report(String step, TextDocument document, GlyphFactory glyphs) {
        System.out.println(step + ": " + document.characterCount() + " characters on screen, "
                + glyphs.created() + " glyph objects");
    }
}
