package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod;

import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.Report;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.classic.CsvExporter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.classic.DocumentExporter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.classic.HtmlExporter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.classic.MarkdownExporter;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.export.modern.ExportFormat;
import java.util.List;

/**
 * Run: {@code java modules/m02-creational-factories/src/main/java/io/github/aliturgutbozkurt/patterns/m02/examples/factorymethod/ExportDemo.java}
 *
 * @see "m02 lesson, section Factory Method"
 */
public final class ExportDemo {

    private ExportDemo() {}

    public static void main(String[] args) {
        var report = new Report("Stock report", List.of("Product", "Qty", "Note"), List.of(
                List.of("Pen", "3", "blue, fine"),
                List.of("Notebook", "5", "A5 <ruled>")));

        System.out.println("== classic: one creator subclass per format ==");
        List<DocumentExporter> exporters = List.of(new MarkdownExporter(), new HtmlExporter(), new CsvExporter());
        var classicOutput = new StringBuilder();
        for (DocumentExporter exporter : exporters) {
            String text = exporter.export(report);
            classicOutput.append(text);
            System.out.println("-- " + exporter.getClass().getSimpleName());
            System.out.print(text);
        }

        var modernOutput = new StringBuilder();
        for (ExportFormat format : ExportFormat.values()) {
            modernOutput.append(format.exporter().export(report));
        }
        System.out.println("modern (enum of constructor references) gives the same text: "
                + classicOutput.toString().equals(modernOutput.toString()));
    }
}
