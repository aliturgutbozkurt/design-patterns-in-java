package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document;

import java.util.List;
import java.util.Objects;

/**
 * A block of a rich-text document. Blocks hold {@link Inline} content; the records copy their lists, so a document
 * is deeply immutable.
 *
 * @see "m08 lesson, section Visitor — second example"
 */
public sealed interface Block {

    record Heading(int level, String text) implements Block {
        public Heading {
            if (level < 1 || level > 6) {
                throw new IllegalArgumentException("heading level must be 1..6: " + level);
            }
            Objects.requireNonNull(text, "text");
        }
    }

    record Paragraph(List<Inline> content) implements Block {
        public Paragraph {
            content = List.copyOf(content);
        }
    }

    /** Each item is its own list of inlines. */
    record BulletList(List<List<Inline>> items) implements Block {
        public BulletList {
            items = items.stream().<List<Inline>>map(List::copyOf).toList();
        }
    }

    record CodeBlock(String language, String code) implements Block {
        public CodeBlock {
            Objects.requireNonNull(language, "language");
            Objects.requireNonNull(code, "code");
        }
    }
}
