package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic.ImportReport.Rejection;
import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DataImporterTest {

    static final String CSV = """
            sku,name,price
            A-1,Pencil,1.20
            A-2,Notebook,abc
            A-3,Eraser,0.00
            A-4,Ruler
            A-5,Stapler,7.50
            """;

    static final String JSON_LINES = """
            {"sku":"A-1","name":"Pencil","price":1.20}
            {"sku":"A-2","name":"Notebook","price":"abc"}
            {"sku":"A-3","name":"Eraser","price":0.00}
            {"sku":"A-4","name":"Ruler"}
            {"sku":"A-5","name":"Stapler","price":7.50}
            """;

    @Test
    void csvImportSkipsAndReportsInvalidRowsWithLineNumbers() {
        var store = new InMemoryProductStore();
        ImportReport report = new CsvProductImporter(store).importData(CSV);
        assertThat(report.importedSkus()).containsExactly("A-1", "A-3", "A-5");
        assertThat(report.rejected()).containsExactly(
                new Rejection(3, "price is not a number: abc"),
                new Rejection(5, "missing field: price"));
        assertThat(store.all()).containsExactly(
                new Product("A-1", "Pencil", new BigDecimal("1.20")),
                new Product("A-3", "Eraser", new BigDecimal("0.00")),
                new Product("A-5", "Stapler", new BigDecimal("7.50")));
    }

    @Test
    void jsonLinesImportReportsItsOwnLineNumbers() {
        ImportReport report = new JsonLinesProductImporter(new InMemoryProductStore()).importData(JSON_LINES);
        assertThat(report.rejected()).containsExactly(
                new Rejection(2, "price is not a number: abc"),
                new Rejection(4, "missing field: price"));
    }

    @Test
    void csvAndJsonLinesWithTheSameDataImportTheSameProductsForTheSameReasons() {
        var csvStore = new InMemoryProductStore();
        var jsonStore = new InMemoryProductStore();
        ImportReport csv = new CsvProductImporter(csvStore).importData(CSV);
        ImportReport json = new JsonLinesProductImporter(jsonStore).importData(JSON_LINES);
        assertThat(json.importedSkus()).isEqualTo(csv.importedSkus());
        assertThat(json.rejected()).extracting(Rejection::reason)
                .isEqualTo(csv.rejected().stream().map(Rejection::reason).toList());
        assertThat(jsonStore.all()).isEqualTo(csvStore.all());
    }

    @Test
    void stepsRunInTheOrderTheTemplateFixes() {
        var trace = new ArrayList<String>();
        DataImporter recording = new DataImporter() {
            @Override
            protected List<RawRow> parse(String input) {
                trace.add("parse");
                return CsvFormat.parse(input);
            }

            @Override
            protected boolean validate(Product product) {
                trace.add("validate " + product.sku());
                return !product.sku().equals("A-3");
            }

            @Override
            protected void save(Product product) {
                trace.add("save " + product.sku());
            }

            @Override
            protected void onError(Rejection rejection) {
                trace.add("error line " + rejection.line());
            }
        };
        recording.importData("sku,name,price\nA-1,Pencil,1.20\nA-2,Notebook,abc\nA-3,Eraser,0.50\n");
        assertThat(trace).containsExactly(
                "parse", "validate A-1", "save A-1", "error line 3", "validate A-3", "error line 4");
    }

    @Test
    void failedValidationIsReported() {
        DataImporter strict = new CsvProductImporter(new InMemoryProductStore()) {
            @Override
            protected boolean validate(Product product) {
                return product.price().signum() > 0;
            }
        };
        assertThat(strict.importData(CSV).rejected()).contains(new Rejection(4, "failed validation"));
    }

    @Test
    void templateMethodIsFinal() throws NoSuchMethodException {
        int modifiers = DataImporter.class.getMethod("importData", String.class).getModifiers();
        assertThat(Modifier.isFinal(modifiers)).isTrue();
    }

    @Test
    void malformedJsonAbortsTheImportWithItsLineNumber() {
        var importer = new JsonLinesProductImporter(new InMemoryProductStore());
        assertThatIllegalArgumentException()
                .isThrownBy(() -> importer.importData("{\"sku\":\"A-1\"}\n{\"sku\" \"A-2\"}\n"))
                .withMessageStartingWith("line 2: expected ':'");
    }

    @Test
    void jsonParserHandlesEscapesAndNumbers() {
        List<RawRow> rows = JsonLinesFormat.parse("""
                { "sku" : "Q-1", "name" : "Say \\"hi\\"", "price" : -1.5e2 }
                """);
        assertThat(rows).containsExactly(new RawRow(1,
                Map.of("sku", "Q-1", "name", "Say \"hi\"", "price", "-1.5e2")));
    }

    @Test
    void productRejectsNegativePrices() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Product("X", "Broken", new BigDecimal("-1")))
                .withMessage("price must be >= 0: -1");
    }

    @Test
    void demoPrintsReportsForBothFormatsAndAStrictSubclass() {
        assertThat(Console.capture(() -> ImporterDemo.main(new String[0]))).isEqualTo("""
                CSV:        imported [A-1, A-3, A-5]; rejected [line 3: price is not a number: abc, line 5: missing field: price]
                JSON Lines: imported [A-1, A-3, A-5]; rejected [line 2: price is not a number: abc, line 4: missing field: price]
                strict CSV: imported [A-1, A-5]; rejected [line 3: price is not a number: abc, line 4: failed validation, line 5: missing field: price]
                store: A-1 Pencil 1.20, A-3 Eraser 0.00, A-5 Stapler 7.50
                """);
    }
}
