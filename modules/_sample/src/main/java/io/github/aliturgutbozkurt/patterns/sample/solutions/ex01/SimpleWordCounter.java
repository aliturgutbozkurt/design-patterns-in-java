package io.github.aliturgutbozkurt.patterns.sample.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.sample.exercises.ex01.WordCounter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** Reference solution for exercise 01. */
public class SimpleWordCounter implements WordCounter {

    @Override
    public Map<String, Integer> count(String text) {
        Objects.requireNonNull(text, "text");
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+"))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toMap(word -> word, _ -> 1, Integer::sum));
    }
}
