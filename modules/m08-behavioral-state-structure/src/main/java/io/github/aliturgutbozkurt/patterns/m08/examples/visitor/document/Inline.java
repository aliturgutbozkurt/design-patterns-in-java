package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document;

import java.util.List;
import java.util.Objects;

/**
 * Inline content of a paragraph or list item. {@code Emphasis} contains further inlines, so this is a Composite of
 * its own, nested inside the {@link Block} Composite.
 *
 * @see "m08 lesson, section Visitor — second example"
 */
public sealed interface Inline {

    record Text(String text) implements Inline {
        public Text {
            Objects.requireNonNull(text, "text");
        }
    }

    record Emphasis(List<Inline> content) implements Inline {
        public Emphasis {
            content = List.copyOf(content);
        }
    }

    record Code(String code) implements Inline {
        public Code {
            Objects.requireNonNull(code, "code");
        }
    }

    record Link(String label, String url) implements Inline {
        public Link {
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(url, "url");
        }
    }
}
