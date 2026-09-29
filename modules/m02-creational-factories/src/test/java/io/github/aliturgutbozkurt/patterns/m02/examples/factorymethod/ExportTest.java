package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.Formatter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.Report;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.classic.CsvExporter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.classic.DocumentExporter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.classic.HtmlExporter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.classic.MarkdownExporter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.modern.ExportFormat;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.modern.Exporter;
import io.github.aliturgutbozkurt.patterns.m02.support.Console;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ExportTest {

    static final Report REPORT = new Report("Stock report", List.of("Product", "Qty", "Note"), List.of(
            List.of("Pen", "3", "blue, fine"),
            List.of("Notebook", "5", "A5 <ruled> & \"dotted\"")));

    @Test
    void markdownExport() {
        assertThat(new MarkdownExporter().export(REPORT)).isEqualTo("""
                # Stock report

                | Product | Qty | Note |
                | --- | --- | --- |
                | Pen | 3 | blue, fine |
                | Notebook | 5 | A5 <ruled> & "dotted" |
                """);
    }

    @Test
    void htmlExportEscapesSpecialCharacters() {
        assertThat(new HtmlExporter().export(REPORT)).isEqualTo("""
                <h1>Stock report</h1>
                <table>
                  <tr><th>Product</th><th>Qty</th><th>Note</th></tr>
                  <tr><td>Pen</td><td>3</td><td>blue, fine</td></tr>
                  <tr><td>Notebook</td><td>5</td><td>A5 &lt;ruled&gt; &amp; &quot;dotted&quot;</td></tr>
                </table>
                """);
    }

    @Test
    void csvExportQuotesCellsWithCommasOrQuotes() {
        assertThat(new CsvExporter().export(REPORT)).isEqualTo("""
                Product,Qty,Note
                Pen,3,"blue, fine"
                Notebook,5,"A5 <ruled> & ""dotted\"""
                """);
    }

    @Test
    void modernExporterMatchesTheClassicOneForEveryFormat() {
        Map<ExportFormat, DocumentExporter> classic = Map.of(
                ExportFormat.MARKDOWN, new MarkdownExporter(),
                ExportFormat.HTML, new HtmlExporter(),
                ExportFormat.CSV, new CsvExporter());
        for (ExportFormat format : ExportFormat.values()) {
            assertThat(format.exporter().export(REPORT)).isEqualTo(classic.get(format).export(REPORT));
        }
    }

    @Test
    void aNewFormatNeedsOnlyANewFormatter() {
        Formatter plain = new Formatter() {
            @Override public String begin(String title) { return title.toUpperCase(java.util.Locale.ROOT) + "\n"; }
            @Override public String header(List<String> cells) { return ""; }
            @Override public String row(List<String> cells) { return String.join(" / ", cells) + "\n"; }
            @Override public String end() { return "-- end --\n"; }
        };
        assertThat(new Exporter(() -> plain).export(REPORT)).isEqualTo("""
                STOCK REPORT
                Pen / 3 / blue, fine
                Notebook / 5 / A5 <ruled> & "dotted"
                -- end --
                """);
    }

    @Test
    void demoPrintsEveryFormat() {
        String output = Console.capture(() -> ExportDemo.main(new String[0]));
        assertThat(output).startsWith("== classic: one creator subclass per format ==\n")
                .contains("<h1>Stock report</h1>")
                .endsWith("modern (enum of constructor references) gives the same text: true\n");
    }
}
