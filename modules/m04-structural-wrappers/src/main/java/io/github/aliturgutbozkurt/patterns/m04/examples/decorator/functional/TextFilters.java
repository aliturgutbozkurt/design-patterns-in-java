package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional;

import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Decorations as functions: each factory returns one small text transformation that can be stacked with the others.
 *
 * @see "m04 lesson, section Decorator"
 */
public final class TextFilters {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private TextFilters() {}

    public static UnaryOperator<String> trim() {
        return String::strip;
    }

    public static UnaryOperator<String> collapseSpaces() {
        return text -> WHITESPACE.matcher(text).replaceAll(" ");
    }

    /** Replaces each whole banned word (ignoring case) with one {@code *} per letter. */
    public static UnaryOperator<String> censor(Set<String> bannedWords) {
        if (bannedWords.isEmpty()) {
            return UnaryOperator.identity();
        }
        Pattern banned = Pattern.compile(
                bannedWords.stream().sorted().map(Pattern::quote).collect(Collectors.joining("|", "\\b(?:", ")\\b")),
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        return text -> banned.matcher(text).replaceAll(match -> "*".repeat(match.group().length()));
    }

    public static UnaryOperator<String> truncate(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength must not be negative: " + maxLength);
        }
        return text -> text.length() <= maxLength ? text : text.substring(0, maxLength);
    }
}
