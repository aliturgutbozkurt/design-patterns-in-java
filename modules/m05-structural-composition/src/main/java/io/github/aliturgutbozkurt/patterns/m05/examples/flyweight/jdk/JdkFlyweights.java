package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.jdk;

import java.time.LocalDate;
import java.util.Currency;
import java.util.Locale;

/**
 * The JDK's own flyweights, as small identity checks. {@code ==} here is deliberate: it asks "is this the
 * <em>same object</em>?", which is exactly what a flyweight cache changes. In application code, compare values
 * with {@code equals}.
 *
 * @see "m05 lesson, section Flyweight — the JDK's own flyweights"
 */
public final class JdkFlyweights {

    private JdkFlyweights() {}

    /** Guaranteed {@code true} for -128..127 (JLS §5.1.7); anything else is up to the JVM. */
    public static boolean boxedIntegersIdentical(int value) {
        return Integer.valueOf(value) == Integer.valueOf(value);
    }

    /** Always {@code true}: value equality does not depend on caching. */
    public static boolean boxedIntegersEqual(int value) {
        return Integer.valueOf(value).equals(Integer.valueOf(value));
    }

    /** {@code Boolean.valueOf} only ever returns {@link Boolean#TRUE} or {@link Boolean#FALSE}. */
    public static boolean booleanIsCanonical(boolean value) {
        return Boolean.valueOf(value) == (value ? Boolean.TRUE : Boolean.FALSE);
    }

    /** Guaranteed {@code true} for the ASCII range, U+0000..U+007F. */
    public static boolean boxedCharactersIdentical(char value) {
        return Character.valueOf(value) == Character.valueOf(value);
    }

    /** {@link Currency} never has more than one instance per currency, however you ask for it. */
    public static boolean currencyShared(String code, Locale locale) {
        return Currency.getInstance(code) == Currency.getInstance(locale);
    }

    /** Not guaranteed either way: {@link LocalDate} is a value-based class, so its identity means nothing. */
    public static boolean datesIdentical(LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth()) == date;
    }

    /** Always {@code true}. */
    public static boolean datesEqual(LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth()).equals(date);
    }
}
