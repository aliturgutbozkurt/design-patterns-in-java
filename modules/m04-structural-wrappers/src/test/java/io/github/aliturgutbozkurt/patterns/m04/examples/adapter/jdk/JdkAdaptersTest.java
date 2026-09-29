package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.jdk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

class JdkAdaptersTest {

    @Test
    void inputStreamReaderAdaptsBytesToChars() throws IOException {
        byte[] bytes = "çğıİöşü".getBytes(StandardCharsets.UTF_8);
        assertThat(bytes).hasSize(14);
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
            String text = reader.readAllAsString();
            assertThat(text).isEqualTo("çğıİöşü").hasSize(7);
        }
    }

    @Test
    void arraysAsListIsAFixedSizeViewThatWritesThrough() {
        String[] seats = {"1A", "1B", "1C"};
        List<String> view = Arrays.asList(seats);
        view.set(1, "taken");
        assertThat(seats).containsExactly("1A", "taken", "1C");
        assertThatThrownBy(() -> view.add("1D")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void enumerationAndIteratorAreBridgedBothWays() {
        Enumeration<String> legacy = Collections.enumeration(List.of("alpha", "beta", "gamma"));
        Iterator<String> modern = legacy.asIterator();
        List<String> seen = new ArrayList<>();
        modern.forEachRemaining(seen::add);
        assertThat(seen).containsExactly("alpha", "beta", "gamma");
    }

    @Test
    void demoPrintsTheThreeJdkAdapters() {
        assertThat(Console.capture(() -> JdkAdaptersDemo.main(new String[0]))).isEqualTo("""
                InputStreamReader: 14 bytes -> 7 chars: çğıİöşü
                Arrays.asList: set(1) wrote through -> array is [1A, taken, 1C]
                Arrays.asList: add -> UnsupportedOperationException (fixed-size view)
                Enumeration.asIterator: alpha, beta, gamma
                """);
    }
}
