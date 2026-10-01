package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.compression;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/strategy/compression/CompressionDemo.java}
 *
 * @see "m06 lesson, section Strategy"
 */
public final class CompressionDemo {

    private CompressionDemo() {}

    public static void main(String[] args) {
        byte[] sample = "AAAABBBCC".getBytes(StandardCharsets.US_ASCII);
        System.out.println("run-length of \"AAAABBBCC\": " + Arrays.toString(Codecs.runLength().compress().apply(sample)));

        List<Codec> codecs = List.of(Codecs.identity(), Codecs.runLength(), Codecs.gzip());
        byte[] repetitive = ("A".repeat(500) + "B".repeat(500)).getBytes(StandardCharsets.US_ASCII);
        byte[] text = "the quick brown fox jumps over the lazy dog".getBytes(StandardCharsets.US_ASCII);

        System.out.println(repetitive.length + " repetitive bytes:");
        for (Codec codec : codecs) {
            System.out.println("  " + new Archiver(codec).report(repetitive));
        }
        System.out.println(text.length + " bytes of text:");
        for (Codec codec : codecs) {
            System.out.println("  " + new Archiver(codec).report(text));
        }
    }
}
