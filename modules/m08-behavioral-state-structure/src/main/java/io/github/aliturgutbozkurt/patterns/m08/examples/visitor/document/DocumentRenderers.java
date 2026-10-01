package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document;

import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.BulletList;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.CodeBlock;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.Heading;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.Paragraph;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Code;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Emphasis;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Link;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Text;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Five operations over two nested sealed hierarchies, with no visitor interface: each is a recursive exhaustive
 * {@code switch} with record patterns. This is the "Visitor over a Composite" of m05's documents.
 *
 * @see "m08 lesson, section Visitor — second example"
 */
public final class DocumentRenderers {

    private DocumentRenderers() {}

    public static String toHtml(Document document) {
        return document.blocks().stream().map(DocumentRenderers::html).collect(Collectors.joining("\n"));
    }

    public static String toPlainText(Document document) {
        return document.blocks().stream().map(DocumentRenderers::plain).collect(Collectors.joining("\n"));
    }

    /** Headings only, indented two spaces per level; levels deeper than 3 are listed as "minor". */
    public static List<String> outline(Document document) {
        return document.blocks().stream()
                .flatMap(block -> switch (block) {
                    case Heading(var level, var text) when level > 3 -> Stream.of("      (minor) " + text);
                    case Heading(var level, var text) -> Stream.of("  ".repeat(level - 1) + text);
                    case Paragraph _, BulletList _, CodeBlock _ -> Stream.empty();
                })
                .toList();
    }

    /** Every link in document order, however deeply it is nested. */
    public static List<Link> links(Document document) {
        return document.blocks().stream()
                .flatMap(block -> switch (block) {
                    case Paragraph(var content) -> linksIn(content);
                    case BulletList(var items) -> items.stream().flatMap(DocumentRenderers::linksIn);
                    case Heading _, CodeBlock _ -> Stream.empty();
                })
                .toList();
    }

    /** Words (tokens with a letter or digit) in the readable text; code blocks do not count. */
    public static int wordCount(Document document) {
        return document.blocks().stream()
                .mapToInt(block -> switch (block) {
                    case CodeBlock _ -> 0;
                    case Heading _, Paragraph _, BulletList _ -> words(plain(block));
                })
                .sum();
    }

    private static String html(Block block) {
        return switch (block) {
            case Heading(var level, var text) -> "<h" + level + ">" + escape(text) + "</h" + level + ">";
            case Paragraph(var content) -> "<p>" + html(content) + "</p>";
            case BulletList(var items) -> items.stream()
                    .map(item -> "<li>" + html(item) + "</li>")
                    .collect(Collectors.joining("\n", "<ul>\n", "\n</ul>"));
            case CodeBlock(var language, var code) ->
                    "<pre><code class=\"language-" + escape(language) + "\">" + escape(code) + "</code></pre>";
        };
    }

    private static String html(List<Inline> content) {
        return content.stream()
                .map(inline -> switch (inline) {
                    case Text(var text) -> escape(text);
                    case Emphasis(List<Inline> inner) -> "<em>" + html(inner) + "</em>";
                    case Code(var code) -> "<code>" + escape(code) + "</code>";
                    case Link(var label, var url) -> "<a href=\"" + escape(url) + "\">" + escape(label) + "</a>";
                })
                .collect(Collectors.joining());
    }

    private static String plain(Block block) {
        return switch (block) {
            case Heading(_, var text) -> text;
            case Paragraph(var content) -> plain(content);
            case BulletList(var items) -> items.stream().map(item -> "- " + plain(item))
                    .collect(Collectors.joining("\n"));
            case CodeBlock(_, var code) -> code;
        };
    }

    private static String plain(List<Inline> content) {
        return content.stream()
                .map(inline -> switch (inline) {
                    case Text(var text) -> text;
                    case Emphasis(var inner) -> plain(inner);
                    case Code(var code) -> code;
                    case Link(var label, _) -> label;
                })
                .collect(Collectors.joining());
    }

    private static Stream<Link> linksIn(List<Inline> content) {
        return content.stream().flatMap(inline -> switch (inline) {
            case Link link -> Stream.of(link);
            case Emphasis(var inner) -> linksIn(inner);
            case Text _, Code _ -> Stream.empty();
        });
    }

    private static int words(String text) {
        return (int) Arrays.stream(text.split("\\s+")).filter(word -> word.matches(".*[\\p{L}\\p{N}].*")).count();
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
