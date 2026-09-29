package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight;

import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.jdk.JdkFlyweights;
import java.time.LocalDate;
import java.util.Locale;

/** Run: {@code java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/flyweight/JdkFlyweightsDemo.java} */
public final class JdkFlyweightsDemo {

    private JdkFlyweightsDemo() {}

    public static void main(String[] args) {
        String cached = "(guaranteed: -128..127 are cached)";
        show("Integer.valueOf(127) == Integer.valueOf(127)", JdkFlyweights.boxedIntegersIdentical(127), cached);
        show("Integer.valueOf(-128) == Integer.valueOf(-128)", JdkFlyweights.boxedIntegersIdentical(-128), cached);
        show("Integer.valueOf(128) == Integer.valueOf(128)", JdkFlyweights.boxedIntegersIdentical(128),
                "(not guaranteed either way)");
        show("Integer.valueOf(128).equals(Integer.valueOf(128))", JdkFlyweights.boxedIntegersEqual(128),
                "(always right)");
        show("Boolean.valueOf(true) == Boolean.TRUE", JdkFlyweights.booleanIsCanonical(true), "(guaranteed)");
        show("Character.valueOf('A') == Character.valueOf('A')", JdkFlyweights.boxedCharactersIdentical('A'),
                "(guaranteed: \\u0000..\\u007f are cached)");
        show("Currency.getInstance(\"EUR\") == Currency.getInstance(Locale.GERMANY)",
                JdkFlyweights.currencyShared("EUR", Locale.GERMANY), "(one instance per currency)");

        LocalDate day = LocalDate.of(2026, 9, 29);
        show("LocalDate.of(2026, 9, 29) == LocalDate.of(2026, 9, 29)", JdkFlyweights.datesIdentical(day),
                "(not guaranteed: value-based class, never use ==)");
        show("LocalDate.of(2026, 9, 29).equals(LocalDate.of(2026, 9, 29))", JdkFlyweights.datesEqual(day),
                "(always right)");
    }

    private static void show(String expression, boolean result, String note) {
        System.out.println(expression + ": " + result + " " + note);
    }
}
