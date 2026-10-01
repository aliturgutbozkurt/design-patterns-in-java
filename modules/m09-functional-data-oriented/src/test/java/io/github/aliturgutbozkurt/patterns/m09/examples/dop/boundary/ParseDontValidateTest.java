package io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary.ImportedRow.Accepted;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary.ImportedRow.Rejected;
import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ParseDontValidateTest {

    private final CsvOrderImporter importer = new CsvOrderImporter();

    @Test
    void importerTurnsEveryRowIntoAcceptedOrRejected() {
        assertThat(importer.parse(ParseDontValidateDemo.CSV)).containsExactly(
                new Accepted(1, line("MUG-0001", 2, 12_50)),
                new Accepted(2, line("TEE-0002", 1, 20_00)),
                new Rejected(3, "sku must match AAA-9999: \"mug-3\""),
                new Rejected(4, "quantity must be 1..99: 0"),
                new Rejected(5, "quantity is not a number: \"two\""),
                new Rejected(6, "expected 3 fields (sku,quantity,unitPriceCents) but got 2"));
    }

    @Test
    void valueConstructorsNameTheFieldAndTheBadValue() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Sku("ab-12"))
                .withMessage("sku must match AAA-9999: \"ab-12\"");
        assertThatIllegalArgumentException().isThrownBy(() -> new Quantity(100))
                .withMessage("quantity must be 1..99: 100");
        assertThatIllegalArgumentException().isThrownBy(() -> new Email("nobody"))
                .withMessage("email must look like name@domain.tld: \"nobody\"");
        assertThatIllegalArgumentException().isThrownBy(() -> new OrderLine(new Sku("MUG-0001"), new Quantity(1), -1))
                .withMessage("unitPriceCents must not be negative: -1");
    }

    @Test
    void emailIsTrimmedAndLowerCased() {
        assertThat(new Email("  Ali@Example.COM ")).isEqualTo(new Email("ali@example.com"));
        assertThat(new Email("  Ali@Example.COM ").value()).isEqualTo("ali@example.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {",,,", "a,b,c,d", "MUG-0001,1,-5", "MUG-0001,1,9999999999999999999", "  "})
    void importerNeverThrowsForBadRows(String row) {
        assertThatNoException().isThrownBy(() -> importer.parse(row));
        assertThat(importer.parse(row)).singleElement().isInstanceOf(Rejected.class);
    }

    @Test
    void importerRejectsOnlyNullInput() {
        assertThatNullPointerException().isThrownBy(() -> importer.parse(null));
    }

    @Test
    void totalOfTheAcceptedLinesIsExact() {
        List<OrderLine> accepted = CsvOrderImporter.acceptedLines(importer.parse(ParseDontValidateDemo.CSV));
        assertThat(accepted).hasSize(2);
        assertThat(OrderLines.totalCents(accepted)).isEqualTo(45_00);
    }

    @Test
    void demoPrintsAcceptedAndRejectedRows() {
        assertThat(Console.capture(() -> ParseDontValidateDemo.main(new String[0]))).isEqualTo("""
                -- the boundary: text in, typed rows out
                line 1: accepted MUG-0001 x 2 @ 1250
                line 2: accepted TEE-0002 x 1 @ 2000
                line 3: rejected (sku must match AAA-9999: "mug-3")
                line 4: rejected (quantity must be 1..99: 0)
                line 5: rejected (quantity is not a number: "two")
                line 6: rejected (expected 3 fields (sku,quantity,unitPriceCents) but got 2)
                -- the core: only valid values, no checks left
                accepted lines: 2, total 4500 cents
                -- values are normalised once, when they are built
                new Email("  Ali@Example.COM ") = ali@example.com
                """);
    }

    private static OrderLine line(String sku, int quantity, long unitPriceCents) {
        return new OrderLine(new Sku(sku), new Quantity(quantity), unitPriceCents);
    }
}
