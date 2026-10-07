package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/** Reads the expected outputs in {@code src/test/resources/acceptance/} (shipped in the test-jar). */
public final class ExpectedOutput {

    private ExpectedOutput() {
    }

    /** The content of {@code acceptance/<name>} with {@code \n} line ends. */
    public static String read(String name) {
        try (InputStream in = ExpectedOutput.class.getResourceAsStream("/acceptance/" + name)) {
            if (in == null) {
                throw new IllegalStateException("missing test resource acceptance/" + name);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
