package io.github.aliturgutbozkurt.patterns.sample.exercises.ex01;

import java.util.Map;

/**
 * GIVEN — do not modify. Counts word occurrences in a text.
 *
 * <p>Words are sequences of letters or digits, compared case-insensitively (lower-cased in the result).
 */
public interface WordCounter {

    /**
     * @param text any text, not {@code null}
     * @return word → occurrences; empty when the text has no words
     * @throws NullPointerException if {@code text} is {@code null}
     */
    Map<String, Integer> count(String text);
}
