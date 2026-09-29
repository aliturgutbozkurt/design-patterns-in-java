package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.glyphs.Glyph;
import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.glyphs.GlyphFactory;
import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.glyphs.TextDocument;
import io.github.aliturgutbozkurt.patterns.m05.support.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class GlyphTest {

    private final GlyphFactory factory = new GlyphFactory();

    @Test
    void sameKeyGivesTheSameInstance() {
        Glyph first = factory.glyph('a', "Serif", 12);
        assertThat(factory.glyph('a', "Serif", 12)).isSameAs(first);
        assertThat(factory.created()).isEqualTo(1);
    }

    @Test
    void differentFontOrSizeGivesADifferentInstance() {
        Glyph serif = factory.glyph('a', "Serif", 12);
        assertThat(factory.glyph('a', "Sans", 12)).isNotSameAs(serif);
        assertThat(factory.glyph('a', "Serif", 14)).isNotSameAs(serif);
        assertThat(factory.created()).isEqualTo(3);
    }

    @Test
    void createdEqualsTheNumberOfDistinctKeys() {
        var document = new TextDocument(factory);
        document.type("banana", "Serif", 12);
        assertThat(document.characterCount()).isEqualTo(6);
        assertThat(factory.created()).isEqualTo(3);
        assertThat(document.distinctGlyphs()).isEqualTo(3);
    }

    @Test
    void typingTheSameTextTwiceCreatesNoNewGlyphs() {
        var document = new TextDocument(factory);
        document.type("hello world", "Serif", 12);
        int afterFirst = factory.created();
        document.type("\nhello world", "Serif", 12);
        assertThat(factory.created()).isEqualTo(afterFirst).isEqualTo(8);
        assertThat(document.characterCount()).isEqualTo(22);
    }

    @Test
    void documentKeepsPositionsOutsideTheGlyph() {
        var document = new TextDocument(factory);
        document.type("ab\nb", "Mono", 10);
        List<TextDocument.Placement> placements = document.placements();
        assertThat(placements).extracting(p -> p.row() + ":" + p.column()).containsExactly("0:0", "0:1", "1:0");
        assertThat(placements.get(1).glyph()).isSameAs(placements.get(2).glyph());
        assertThat(document.text()).isEqualTo("ab\nb");
    }

    @Test
    void oneThousandVirtualThreadsCreateExactlyOneGlyph() throws Exception {
        List<Future<Glyph>> results = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1000; i++) {
                results.add(executor.submit(() -> factory.glyph('x', "Serif", 12)));
            }
        }
        Set<Glyph> distinct = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Future<Glyph> result : results) {
            distinct.add(result.get());
        }
        assertThat(distinct).hasSize(1);
        assertThat(factory.created()).isEqualTo(1);
    }

    @Test
    void rejectsInvalidGlyphs() {
        assertThatIllegalArgumentException().isThrownBy(() -> factory.glyph('a', " ", 12));
        assertThatIllegalArgumentException().isThrownBy(() -> factory.glyph('a', "Serif", 0));
    }

    @Test
    void demoPrintsCharactersVersusGlyphObjects() {
        assertThat(Console.capture(() -> TextEditorDemo.main(new String[0]))).isEqualTo("""
                one line: 43 characters on screen, 27 glyph objects
                same line again: 86 characters on screen, 27 glyph objects
                heading "Hello" in Sans 18: 91 characters on screen, 31 glyph objects
                'e' Serif 12 twice -> same instance: true
                'e' Serif 12 vs Sans 18 -> same instance: false
                """);
    }
}
