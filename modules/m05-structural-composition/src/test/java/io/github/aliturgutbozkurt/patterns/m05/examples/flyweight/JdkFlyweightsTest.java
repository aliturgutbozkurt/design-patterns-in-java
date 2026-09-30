package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.jdk.JdkFlyweights;
import io.github.aliturgutbozkurt.patterns.m05.support.Console;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/** Asserts only what the JLS and the JDK specification guarantee — never the result outside the cached ranges. */
class JdkFlyweightsTest {

    @Test
    void boxingMinus128To127IsIdentical() {
        for (int value = -128; value <= 127; value++) {
            assertThat(JdkFlyweights.boxedIntegersIdentical(value)).as("Integer %d", value).isTrue();
        }
    }

    @Test
    void booleanValueOfReturnsTheTwoCanonicalInstances() {
        assertThat(JdkFlyweights.booleanIsCanonical(true)).isTrue();
        assertThat(JdkFlyweights.booleanIsCanonical(false)).isTrue();
    }

    @Test
    void characterValueOfIsCachedForAscii() {
        for (char c = '\u0000'; c <= '\u007f'; c++) {
            assertThat(JdkFlyweights.boxedCharactersIdentical(c)).isTrue();
        }
    }

    @Test
    void currencyHasOneInstancePerCurrency() {
        assertThat(JdkFlyweights.currencyShared("EUR", Locale.GERMANY)).isTrue();
        assertThat(JdkFlyweights.currencyShared("EUR", Locale.FRANCE)).isTrue();
    }

    @Test
    void equalsIsAlwaysRight() {
        for (int value : List.of(-129, 0, 127, 128, 1000, Integer.MAX_VALUE)) {
            assertThat(JdkFlyweights.boxedIntegersEqual(value)).isTrue();
        }
        assertThat(JdkFlyweights.datesEqual(LocalDate.of(2026, 9, 29))).isTrue();
    }

    @Test
    void demoPrintsGuaranteedFactsExactlyAndMarksTheRest() {
        List<String> lines = Console.capture(() -> JdkFlyweightsDemo.main(new String[0])).lines().toList();
        assertThat(lines).hasSize(9);
        assertThat(lines.get(0)).isEqualTo(
                "Integer.valueOf(127) == Integer.valueOf(127): true (guaranteed: -128..127 are cached)");
        assertThat(lines.get(1)).isEqualTo(
                "Integer.valueOf(-128) == Integer.valueOf(-128): true (guaranteed: -128..127 are cached)");
        // Line 2 depends on -XX:AutoBoxCacheMax: only its shape is checked, never true/false.
        assertThat(lines.get(2)).startsWith("Integer.valueOf(128) == Integer.valueOf(128): ")
                .endsWith(" (not guaranteed either way)");
        assertThat(lines.get(3)).isEqualTo(
                "Integer.valueOf(128).equals(Integer.valueOf(128)): true (always right)");
        assertThat(lines.get(4)).isEqualTo("Boolean.valueOf(true) == Boolean.TRUE: true (guaranteed)");
        assertThat(lines.get(5)).isEqualTo(
                "Character.valueOf('A') == Character.valueOf('A'): true (guaranteed: \\u0000..\\u007f are cached)");
        assertThat(lines.get(6)).isEqualTo(
                "Currency.getInstance(\"EUR\") == Currency.getInstance(Locale.GERMANY): true (one instance per currency)");
        assertThat(lines.get(7)).startsWith("LocalDate.of(2026, 9, 29) == LocalDate.of(2026, 9, 29): ")
                .endsWith(" (not guaranteed: value-based class, never use ==)");
        assertThat(lines.get(8)).isEqualTo(
                "LocalDate.of(2026, 9, 29).equals(LocalDate.of(2026, 9, 29)): true (always right)");
    }
}
