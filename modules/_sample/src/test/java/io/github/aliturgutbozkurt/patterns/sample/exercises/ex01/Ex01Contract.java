package io.github.aliturgutbozkurt.patterns.sample.exercises.ex01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

/**
 * The specification of exercise 01 as tests. Both the student's starter code and the reference
 * solution are bound to these same tests (see CONTRIBUTING.md, "Contract tests").
 */
public abstract class Ex01Contract {

    /** Returns the implementation under test. */
    protected abstract WordCounter newCounter();

    @Test
    void countsEachWordCaseInsensitively() {
        assertThat(newCounter().count("Java java JAVA patterns"))
                .containsExactlyInAnyOrderEntriesOf(java.util.Map.of("java", 3, "patterns", 1));
    }

    @Test
    void ignoresPunctuationAndExtraSpaces() {
        assertThat(newCounter().count("  Hello,   world! Hello. "))
                .containsExactlyInAnyOrderEntriesOf(java.util.Map.of("hello", 2, "world", 1));
    }

    @Test
    void returnsEmptyMapForBlankText() {
        assertThat(newCounter().count("   ")).isEmpty();
    }

    @Test
    void rejectsNull() {
        assertThatNullPointerException().isThrownBy(() -> newCounter().count(null));
    }
}
