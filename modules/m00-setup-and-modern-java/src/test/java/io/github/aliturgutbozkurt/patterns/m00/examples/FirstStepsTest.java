package io.github.aliturgutbozkurt.patterns.m00.examples;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/** The first-steps programs live outside the Maven source tree and are run the way students run them. */
class FirstStepsTest {

    @Test
    void compactSourceFileRunsWithoutABuild() throws Exception {
        assertThat(launch("first-steps/Hello.java")).isEqualTo("Hello, Java 27!\nMerhaba, Java 27!\n");
    }

    @Test
    void multiFileProgramRunsWithoutABuild() throws Exception {
        assertThat(launch("first-steps/multifile/Main.java")).isEqualTo("Hello, Ada — from two source files!\n");
    }

    private static String launch(String sourceFile) throws IOException, InterruptedException {
        Path java = Path.of(System.getProperty("java.home"), "bin", "java");
        Process process = new ProcessBuilder(java.toString(), sourceFile).redirectErrorStream(true).start();
        boolean finished = process.waitFor(60, TimeUnit.SECONDS);
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertThat(finished).as("launcher finished in time").isTrue();
        assertThat(process.exitValue()).as(output).isZero();
        return output.replace("\r\n", "\n");
    }
}
