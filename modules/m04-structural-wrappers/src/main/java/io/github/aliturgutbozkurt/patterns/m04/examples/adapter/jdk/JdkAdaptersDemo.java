package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.jdk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/** Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/adapter/jdk/JdkAdaptersDemo.java} */
public final class JdkAdaptersDemo {

    private JdkAdaptersDemo() {}

    public static void main(String[] args) {
        // 1. InputStreamReader adapts a byte stream (InputStream) to a character stream (Reader).
        byte[] bytes = "çğıİöşü".getBytes(StandardCharsets.UTF_8);
        String text = decodeUtf8(bytes);
        System.out.println("InputStreamReader: " + bytes.length + " bytes -> " + text.length() + " chars: " + text);

        // 2. Arrays.asList adapts an array to the List interface: a fixed-size view, not a copy.
        String[] seats = {"1A", "1B", "1C"};
        List<String> view = Arrays.asList(seats);
        view.set(1, "taken");
        System.out.println("Arrays.asList: set(1) wrote through -> array is " + Arrays.toString(seats));
        try {
            view.add("1D");
        } catch (UnsupportedOperationException e) {
            System.out.println("Arrays.asList: add -> UnsupportedOperationException (fixed-size view)");
        }

        // 3. Legacy Enumeration <-> modern Iterator, adapted in both directions.
        Enumeration<String> legacy = Collections.enumeration(List.of("alpha", "beta", "gamma"));
        Iterator<String> modern = legacy.asIterator();
        List<String> names = new ArrayList<>();
        modern.forEachRemaining(names::add);
        System.out.println("Enumeration.asIterator: " + String.join(", ", names));
    }

    private static String decodeUtf8(byte[] bytes) {
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
            return reader.readAllAsString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
