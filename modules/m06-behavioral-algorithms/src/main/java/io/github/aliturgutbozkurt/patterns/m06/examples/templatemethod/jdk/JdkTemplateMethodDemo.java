package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.jdk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/templatemethod/jdk/JdkTemplateMethodDemo.java} */
public final class JdkTemplateMethodDemo {

    private JdkTemplateMethodDemo() {}

    public static void main(String[] args) {
        var countdown = new Countdown(5);  // we wrote get(int) and size(); everything below is inherited
        var line = new StringBuilder("for-each over Countdown(5):");
        for (int n : countdown) {
            line.append(' ').append(n);
        }
        System.out.println(line);
        System.out.println("contains(3)=" + countdown.contains(3) + " indexOf(1)=" + countdown.indexOf(1)
                + " subList(1, 3)=" + countdown.subList(1, 3));
        System.out.println("equals(List.of(5, 4, 3, 2, 1))=" + countdown.equals(List.of(5, 4, 3, 2, 1)));
        try {
            countdown.add(0);
        } catch (UnsupportedOperationException e) {
            System.out.println("add(0) -> UnsupportedOperationException");
        }

        try (var in = new AlphabetStream(10)) {  // we wrote read(); readAllBytes() is inherited
            System.out.println("readAllBytes(): " + new String(in.readAllBytes(), StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        var out = new ByteArrayOutputStream();
        try (var in = new AlphabetStream(26)) {
            long copied = in.transferTo(out);
            System.out.println("transferTo(): " + copied + " bytes, " + out.toString(StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
