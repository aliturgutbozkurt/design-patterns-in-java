package io.github.aliturgutbozkurt.patterns.m09.examples.composition.text;

import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Product URL slugs from six tiny {@code String -> String} steps, each testable on its own and joined with
 * {@link Function#andThen}. Every case conversion names its {@link Locale}: in Turkish, {@code "I"} lower-cases to the
 * dotless {@code "ı"}, which no Unicode decomposition turns back into {@code "i"}.
 *
 * @see "m09 lesson, section Function composition, currying and partial application"
 */
public final class Slugifier {

    private static final Pattern MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern APOSTROPHES = Pattern.compile("['’]");
    private static final Pattern NOT_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");
    private static final Pattern DASH_RUNS = Pattern.compile("-{2,}");
    private static final Pattern EDGE_DASHES = Pattern.compile("^-+|-+$");

    private Slugifier() {}

    /** The whole pipeline for titles written in {@code locale}. */
    public static Function<String, String> forLocale(Locale locale) {
        return lowerCase(locale)
                .andThen(transliterateTurkish())
                .andThen(stripDiacritics())
                .andThen(replaceNonAlphanumeric())
                .andThen(collapseDashes())
                .andThen(trimDashes());
    }

    /** The same pipeline minus the Turkish step, to show why that step is needed. */
    public static Function<String, String> withoutTransliteration(Locale locale) {
        return lowerCase(locale)
                .andThen(stripDiacritics())
                .andThen(replaceNonAlphanumeric())
                .andThen(collapseDashes())
                .andThen(trimDashes());
    }

    public static Function<String, String> lowerCase(Locale locale) {
        return text -> text.toLowerCase(locale);
    }

    /** {@code ı -> i} and {@code İ -> I}: the two letters that NFD cannot simplify. */
    public static Function<String, String> transliterateTurkish() {
        return text -> text.replace('ı', 'i').replace('İ', 'I');
    }

    /** Decomposes (NFD) and drops the combining marks: {@code ş -> s}, {@code ö -> o}, {@code é -> e}. */
    public static Function<String, String> stripDiacritics() {
        return text -> MARKS.matcher(Normalizer.normalize(text, Normalizer.Form.NFD)).replaceAll("");
    }

    /** Drops apostrophes ({@code İstanbul'da}) and turns every other non-ASCII-alphanumeric character into {@code -}. */
    public static Function<String, String> replaceNonAlphanumeric() {
        return text -> NOT_ALPHANUMERIC.matcher(APOSTROPHES.matcher(text).replaceAll("")).replaceAll("-");
    }

    public static Function<String, String> collapseDashes() {
        return text -> DASH_RUNS.matcher(text).replaceAll("-");
    }

    public static Function<String, String> trimDashes() {
        return text -> EDGE_DASHES.matcher(text).replaceAll("");
    }
}
