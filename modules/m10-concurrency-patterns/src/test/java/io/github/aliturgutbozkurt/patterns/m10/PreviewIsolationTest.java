package io.github.aliturgutbozkurt.patterns.m10;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.CrawlerDemo;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * The module compiles with {@code --enable-preview}, but javac marks only the classes that really use a preview API
 * (class-file minor version {@code 0xFFFF}). This test keeps those classes inside {@code examples.structured}, so
 * preview use cannot spread silently (spec m10, decision 1): every other demo must keep running with plain
 * {@code java File.java}, and the assignments must use final APIs only.
 */
class PreviewIsolationTest {

    private static final int PREVIEW_MINOR_VERSION = 0xFFFF;
    private static final String ALLOWED = "io/github/aliturgutbozkurt/patterns/m10/examples/structured/";

    private static Path mainClasses() throws URISyntaxException {
        Path demo = Path.of(CrawlerDemo.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        assertThat(demo).as("main classes directory").isDirectory();
        return demo;
    }

    private static int minorVersion(Path classFile) {
        try (InputStream in = Files.newInputStream(classFile); var data = new DataInputStream(in)) {
            assertThat(data.readInt()).as("magic of %s", classFile).isEqualTo(0xCAFEBABE);
            return data.readUnsignedShort();            // bytes 4-5: minor version
        } catch (IOException e) {
            throw new AssertionError("cannot read " + classFile, e);
        }
    }

    private static List<String> previewClasses(Path root) throws IOException {
        try (Stream<Path> files = Files.walk(root)) {
            return files.filter(file -> file.toString().endsWith(".class"))
                    .filter(file -> minorVersion(file) == PREVIEW_MINOR_VERSION)
                    .map(file -> root.relativize(file).toString().replace('\\', '/'))
                    .sorted()
                    .toList();
        }
    }

    @Test
    void onlyClassesInExamplesStructuredAreMarkedAsPreview() throws Exception {
        List<String> preview = previewClasses(mainClasses());
        assertThat(preview).as("preview-marked classes").isNotEmpty().allMatch(name -> name.startsWith(ALLOWED));
    }

    @Test
    void exercisesAndSolutionsUseFinalApisOnly() throws Exception {
        List<String> preview = previewClasses(mainClasses());
        assertThat(preview).noneMatch(name -> name.contains("/m10/exercises/") || name.contains("/m10/solutions/"));
    }

    @Test
    void theNonPreviewDemosAreNotMarked() throws Exception {
        Path root = mainClasses();
        assertThat(minorVersion(root.resolve("io/github/aliturgutbozkurt/patterns/m10/examples/threadpertask/"
                + "CrawlerDemo.class"))).isZero();
    }
}
