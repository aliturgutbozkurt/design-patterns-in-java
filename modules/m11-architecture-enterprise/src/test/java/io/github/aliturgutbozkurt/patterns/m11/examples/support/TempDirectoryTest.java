package io.github.aliturgutbozkurt.patterns.m11.examples.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class TempDirectoryTest {

    @Test
    void closeDeletesTheDirectoryAndEverythingInIt() throws IOException {
        Path path;
        try (TempDirectory temp = TempDirectory.create("m11-test")) {
            path = temp.path();
            Files.createDirectories(path.resolve("nested"));
            Files.writeString(path.resolve("nested/file.txt"), "x");
            assertThat(path).isDirectory();
        }
        assertThat(path).doesNotExist();
    }

    @Test
    void closingTwiceIsHarmless() {
        TempDirectory temp = TempDirectory.create("m11-test");
        temp.close();
        temp.close();
        assertThat(temp.path()).doesNotExist();
    }
}
