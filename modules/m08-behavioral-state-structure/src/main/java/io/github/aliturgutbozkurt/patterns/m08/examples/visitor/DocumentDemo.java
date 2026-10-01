package io.github.aliturgutbozkurt.patterns.m08.examples.visitor;

import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.BulletList;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.CodeBlock;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.Heading;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.Paragraph;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Document;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.DocumentRenderers;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Code;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Emphasis;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Link;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Text;
import java.util.List;

/** Run: {@code java modules/m08-behavioral-state-structure/src/main/java/io/github/aliturgutbozkurt/patterns/m08/examples/visitor/DocumentDemo.java} */
public final class DocumentDemo {

    private DocumentDemo() {}

    /** The sample document the demo renders. */
    public static Document sample() {
        return new Document(List.of(
                new Heading(1, "Release notes"),
                new Paragraph(List.of(new Text("Version 2 is "), new Emphasis(List.of(new Text("faster"))),
                        new Text(" & safer."))),
                new Heading(2, "Changes"),
                new BulletList(List.of(
                        List.of(new Text("New "), new Code("switch"), new Text(" patterns")),
                        List.of(new Emphasis(List.of(new Text("Read the "),
                                new Link("docs", "https://example.org/docs?a=1&b=2")))))),
                new Heading(4, "Fine print"),
                new CodeBlock("java", "if (a < b && c > d) {}")));
    }

    public static void main(String[] args) {
        Document document = sample();
        System.out.println("--- html");
        System.out.println(DocumentRenderers.toHtml(document));
        System.out.println("--- plain text");
        System.out.println(DocumentRenderers.toPlainText(document));
        System.out.println("--- outline");
        DocumentRenderers.outline(document).forEach(System.out::println);
        System.out.println("--- links");
        DocumentRenderers.links(document).forEach(link -> System.out.println(link.label() + " -> " + link.url()));
        System.out.println("--- words: " + DocumentRenderers.wordCount(document));
    }
}
