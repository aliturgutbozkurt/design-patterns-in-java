package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.functional;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.CsvProductImporter;
import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.ImportReport;
import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.ImportReport.Rejection;
import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.InMemoryProductStore;
import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.JsonLinesProductImporter;
import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.Product;
import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ImporterTest {

    @Test
    void givesTheSameReportsAsTheClassicImporters() {
        assertThat(Importers.csv(_ -> {}).importData(ImporterDemo.CSV))
                .isEqualTo(new CsvProductImporter(new InMemoryProductStore()).importData(ImporterDemo.CSV));
        assertThat(Importers.jsonLines(_ -> {}).importData(ImporterDemo.JSON_LINES))
                .isEqualTo(new JsonLinesProductImporter(new InMemoryProductStore()).importData(ImporterDemo.JSON_LINES));
    }

    @Test
    void sinkReceivesEveryImportedProductInOrder() {
        var saved = new ArrayList<Product>();
        Importers.csv(saved::add).importData(ImporterDemo.CSV);
        assertThat(saved).extracting(Product::sku).containsExactly("A-1", "A-3", "A-5");
    }

    @Test
    void customValidatorLambdaRejectsTheExpectedRows() {
        ImportReport report = Importers.csv(_ -> {})
                .withValidator(product -> product.price().signum() > 0)
                .importData(ImporterDemo.CSV);
        assertThat(report.importedSkus()).containsExactly("A-1", "A-5");
        assertThat(report.rejected()).contains(new Rejection(4, "failed validation"));
    }

    @Test
    void changingOneStepNeedsNoSubclass() {
        Importer strict = Importers.jsonLines(_ -> {}).withValidator(product -> product.name().length() > 6);
        assertThat(strict.getClass()).isEqualTo(Importer.class);
        assertThat(strict.importData(ImporterDemo.JSON_LINES).importedSkus()).containsExactly("A-5");
    }

    @Test
    void demoPrintsTheSameReportsAsTheClassicDemo() {
        assertThat(Console.capture(() -> ImporterDemo.main(new String[0]))).isEqualTo("""
                CSV:        imported [A-1, A-3, A-5]; rejected [line 3: price is not a number: abc, line 5: missing field: price]
                JSON Lines: imported [A-1, A-3, A-5]; rejected [line 2: price is not a number: abc, line 4: missing field: price]
                strict CSV: imported [A-1, A-5]; rejected [line 3: price is not a number: abc, line 4: failed validation, line 5: missing field: price]
                saved by the sink: [A-1, A-3, A-5]
                """);
    }
}
