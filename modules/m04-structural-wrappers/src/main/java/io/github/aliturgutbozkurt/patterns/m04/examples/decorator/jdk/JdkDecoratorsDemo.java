package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.jdk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/** Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/decorator/jdk/JdkDecoratorsDemo.java} */
public final class JdkDecoratorsDemo {

    private JdkDecoratorsDemo() {}

    public static void main(String[] args) {
        List<String> log = List.of(
                "INFO  service started",
                "WARN  disk 91% full",
                "ERROR payment timeout",
                "INFO  service stopped");
        try {
            byte[] gzip = compress(log);
            System.out.println("wrote " + log.size() + " lines -> " + gzip.length + " gzip bytes");

            var counting = new CountingInputStream(new ByteArrayInputStream(gzip));
            List<String> lines;
            try (var reader = new BufferedReader(                       // chars -> lines
                    new InputStreamReader(                               // bytes -> chars
                            new GZIPInputStream(counting),               // gzip -> bytes
                            StandardCharsets.UTF_8))) {
                lines = reader.lines().toList();
            }
            System.out.println("read back " + lines.size() + " lines, errors: "
                    + lines.stream().filter(line -> line.startsWith("ERROR")).toList());
            System.out.println("CountingInputStream saw " + counting.count() + " of " + gzip.length + " bytes");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // A read-only *view* is a decorator (a protection proxy, really); List.copyOf is a *copy*.
        List<String> backing = new ArrayList<>(List.of("a", "b"));
        List<String> view = Collections.unmodifiableList(backing);
        List<String> copy = List.copyOf(backing);
        backing.add("c");
        System.out.println("after backing.add(\"c\"): view " + view + ", copy " + copy);
        try {
            view.add("d");
        } catch (UnsupportedOperationException e) {
            System.out.println("view.add -> UnsupportedOperationException");
        }
    }

    static byte[] compress(List<String> lines) throws IOException {
        var bytes = new ByteArrayOutputStream();
        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(new GZIPOutputStream(bytes), StandardCharsets.UTF_8))) {
            for (String line : lines) {
                writer.write(line);
                writer.write('\n');
            }
        }                                                   // closing the outermost writer finishes the GZIP stream
        return bytes.toByteArray();
    }
}
