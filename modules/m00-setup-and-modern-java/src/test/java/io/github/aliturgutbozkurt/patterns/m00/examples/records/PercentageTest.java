package io.github.aliturgutbozkurt.patterns.m00.examples.records;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m00.support.Console;
import org.junit.jupiter.api.Test;

class PercentageTest {

    @Test
    void parsesTextBeforeDelegatingToTheCanonicalConstructor() {
        assertThat(new Percentage(" 15% ")).isEqualTo(new Percentage(15));
    }

    @Test
    void rejectsOutOfRangeValues() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Percentage("120%"))
                .withMessage("percentage must be between 0 and 100: 120");
        assertThatIllegalArgumentException().isThrownBy(() -> new Percentage(-1));
    }

    @Test
    void rejectsText() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Percentage("fifteen"))
                .withMessage("not a percentage: fifteen");
    }

    @Test
    void computesItsShareOfAPrice() {
        assertThat(new Percentage(15).of(Money.of("20", "EUR"))).isEqualTo(Money.of("3", "EUR"));
    }

    @Test
    void demoPrintsExpectedLines() {
        assertThat(Console.capture(() -> PercentageDemo.main(new String[0]))).isEqualTo("""
                "15%" -> Percentage[value=15]
                15% of 20.00 EUR = 3.00 EUR
                "120%" rejected: percentage must be between 0 and 100: 120
                "abc" rejected: not a percentage: abc
                """);
    }
}
