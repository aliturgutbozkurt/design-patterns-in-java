package io.github.aliturgutbozkurt.patterns.m08.examples.visitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.BulletList;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.CodeBlock;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.Heading;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Block.Paragraph;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Document;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.DocumentRenderers;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Code;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Emphasis;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Link;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document.Inline.Text;
import io.github.aliturgutbozkurt.patterns.m08.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DocumentTest {

    static final Document SAMPLE = DocumentDemo.sample();

    @Test
    void rendersExactHtmlWithEscaping() {
        assertThat(DocumentRenderers.toHtml(SAMPLE)).isEqualTo("""
                <h1>Release notes</h1>
                <p>Version 2 is <em>faster</em> &amp; safer.</p>
                <h2>Changes</h2>
                <ul>
                <li>New <code>switch</code> patterns</li>
                <li><em>Read the <a href="https://example.org/docs?a=1&amp;b=2">docs</a></em></li>
                </ul>
                <h4>Fine print</h4>
                <pre><code class="language-java">if (a &lt; b &amp;&amp; c &gt; d) {}</code></pre>""");
    }

    @Test
    void plainTextStripsMarkup() {
        assertThat(DocumentRenderers.toPlainText(SAMPLE)).isEqualTo("""
                Release notes
                Version 2 is faster & safer.
                Changes
                - New switch patterns
                - Read the docs
                Fine print
                if (a < b && c > d) {}""");
    }

    @Test
    void outlineIndentsByLevelAndMarksDeepHeadingsAsMinor() {
        var doc = new Document(List.of(new Heading(1, "A"), new Heading(2, "B"), new Heading(3, "C"),
                new Heading(4, "D"), new Heading(6, "E"), new Paragraph(List.of(new Text("not a heading")))));
        assertThat(DocumentRenderers.outline(doc)).containsExactly("A", "  B", "    C", "      (minor) D",
                "      (minor) E");
    }

    @Test
    void linksAreFoundInDocumentOrderIncludingInsideEmphasisInsideListItems() {
        var doc = new Document(List.of(
                new Paragraph(List.of(new Link("one", "u1"))),
                new BulletList(List.of(
                        List.of(new Text("x")),
                        List.of(new Emphasis(List.of(new Emphasis(List.of(new Link("two", "u2")))))))),
                new CodeBlock("text", "[not](a link)"),
                new Paragraph(List.of(new Link("three", "u3")))));
        assertThat(DocumentRenderers.links(doc)).containsExactly(new Link("one", "u1"), new Link("two", "u2"),
                new Link("three", "u3"));
    }

    @Test
    void wordCountIgnoresCodeBlocks() {
        assertThat(DocumentRenderers.wordCount(SAMPLE)).isEqualTo(16);
        assertThat(DocumentRenderers.wordCount(new Document(List.of(new CodeBlock("java", "int a = 1;")))))
                .isZero();
    }

    @Test
    void recordsCopyTheirLists() {
        var inlines = new ArrayList<Inline>(List.of(new Text("a")));
        var paragraph = new Paragraph(inlines);
        var item = new ArrayList<Inline>(List.of(new Text("b")));
        var list = new BulletList(List.of(item));
        inlines.add(new Text("c"));
        item.add(new Text("d"));
        assertThat(paragraph.content()).containsExactly(new Text("a")).isUnmodifiable();
        assertThat(list.items().getFirst()).containsExactly(new Text("b")).isUnmodifiable();
    }

    @Test
    void headingLevelOutsideOneToSixIsRejected() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Heading(0, "x"))
                .withMessage("heading level must be 1..6: 0");
        assertThatIllegalArgumentException().isThrownBy(() -> new Heading(7, "x"));
    }

    @Test
    void demoPrintsAllFiveOperations() {
        assertThat(Console.capture(() -> DocumentDemo.main(new String[0]))).isEqualTo("""
                --- html
                <h1>Release notes</h1>
                <p>Version 2 is <em>faster</em> &amp; safer.</p>
                <h2>Changes</h2>
                <ul>
                <li>New <code>switch</code> patterns</li>
                <li><em>Read the <a href="https://example.org/docs?a=1&amp;b=2">docs</a></em></li>
                </ul>
                <h4>Fine print</h4>
                <pre><code class="language-java">if (a &lt; b &amp;&amp; c &gt; d) {}</code></pre>
                --- plain text
                Release notes
                Version 2 is faster & safer.
                Changes
                - New switch patterns
                - Read the docs
                Fine print
                if (a < b && c > d) {}
                --- outline
                Release notes
                  Changes
                      (minor) Fine print
                --- links
                docs -> https://example.org/docs?a=1&b=2
                --- words: 16
                """);
    }
}
