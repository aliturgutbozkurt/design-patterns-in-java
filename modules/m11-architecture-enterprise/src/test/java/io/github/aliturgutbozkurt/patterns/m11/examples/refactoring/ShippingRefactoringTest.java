package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingCalculator;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingMethod;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingMethod.Express;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingMethod.Pickup;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.after.ShippingMethod.Standard;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ShippingRefactoringTest {

    private final io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.before.ShippingCalculator before =
            new io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.shipping.before.ShippingCalculator();
    private final ShippingCalculator after = new ShippingCalculator();

    /** The characterization table: pinned on the old code first, then run against the new one. */
    @ParameterizedTest(name = "{0} {1} g {2} km = {3}")
    @CsvSource(textBlock = """
            STANDARD,    0,   0,  499
            STANDARD, 2000, 500,  499
            STANDARD, 2001, 500,  599
            STANDARD, 3000, 501,  899
            STANDARD, 3001, 501,  999
            EXPRESS,     0,   0,  999
            EXPRESS,  1000, 500,  999
            EXPRESS,  1001, 500, 1199
            EXPRESS,  2500, 501, 2798
            PICKUP,   9000, 900,    0
            """)
    void beforeAndAfterAgreeOnEveryRowIncludingBoundaries(String code, int weight, int distance, long cents) {
        ShippingMethod method = switch (code) {
            case "STANDARD" -> new Standard(weight, distance);
            case "EXPRESS" -> new Express(weight, distance);
            case "PICKUP" -> new Pickup();
            default -> throw new IllegalArgumentException(code);
        };
        assertThat(before.costCents(code, weight, distance)).isEqualTo(cents);
        assertThat(after.costCents(method)).isEqualTo(cents);
    }

    @Test
    void beforeSilentlyReturnsZeroForAnUnknownCode() {
        assertThat(before.costCents("EXPRES", 1500, 600)).isZero(); // documents the bug the sealed type removes
    }

    @Test
    void afterRejectsImpossibleParcels() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Standard(-1, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> new Express(0, -5));
    }

    @Test
    void demoPrintsIdenticalCostsAndTheTypo() {
        assertThat(Console.capture(() -> ShippingRefactoringDemo.main(new String[0]))).isEqualTo("""
                STANDARD 2500 g 100 km before 5.99 | after 5.99
                EXPRESS  1500 g 600 km before 23.98 | after 23.98
                PICKUP                 before 0.00 | after 0.00
                EXPRES (typo)          before 0.00 | after: does not compile — there is no such record
                """);
    }
}
