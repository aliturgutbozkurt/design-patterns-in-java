package io.github.aliturgutbozkurt.patterns.m08.examples.visitor;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.files.DiskUsage;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.files.DiskUsageReport;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.files.DiskUsageReport.Usage;
import io.github.aliturgutbozkurt.patterns.m08.support.Console;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DiskUsageTest {

    @TempDir
    Path root;

    private static void write(Path file, int bytes) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, "x".repeat(bytes));
    }

    @Test
    void totalsPerExtensionAndFileCountAreExact() throws IOException {
        DiskUsageDemo.createSampleTree(root);
        DiskUsageReport report = DiskUsage.scan(root, Set.of("build"));
        assertThat(report.byExtension()).containsExactlyInAnyOrderEntriesOf(Map.of(
                "(none)", new Usage(1, 50), "java", new Usage(2, 500), "md", new Usage(2, 200),
                "txt", new Usage(1, 40)));
        assertThat(report.byExtension().keySet()).containsExactly("(none)", "java", "md", "txt");
        assertThat(report.totalFiles()).isEqualTo(6);
        assertThat(report.totalBytes()).isEqualTo(790);
        assertThat(report.stoppedEarly()).isFalse();
    }

    @Test
    void ignoredDirectoriesAreSkippedWithSkipSubtree() throws IOException {
        DiskUsageDemo.createSampleTree(root);
        DiskUsageReport report = DiskUsage.scan(root, Set.of("build"));
        assertThat(report.byExtension()).doesNotContainKeys("class", "jar");
        assertThat(report.skippedDirectories()).containsExactly("build", "src/build");

        DiskUsageReport everything = DiskUsage.scan(root, Set.of());
        assertThat(everything.byExtension()).containsKeys("class", "jar");
        assertThat(everything.totalFiles()).isEqualTo(8);
    }

    @Test
    void terminateAfterTheByteBudgetStopsTheWalk() throws IOException {
        for (String name : List.of("a", "b", "c", "d", "e")) {
            write(root.resolve(name + ".dat"), 100);
        }
        DiskUsageReport report = DiskUsage.scan(root, Set.of(), 250);
        assertThat(report.stoppedEarly()).isTrue();
        assertThat(report.totalFiles()).isEqualTo(3);
        assertThat(report.totalBytes()).isEqualTo(300);
    }

    @Test
    void theReportDoesNotDependOnCreationOrder(@TempDir Path other) throws IOException {
        DiskUsageDemo.createSampleTree(root);
        for (String file : List.of("src/build/App.class", "src/Util.java", "build/app.jar", "LICENSE",
                "docs/notes.txt", "src/App.java", "README.md", "docs/guide.md")) {
            Path source = root.resolve(file);
            write(other.resolve(file), (int) Files.size(source));
        }
        assertThat(DiskUsage.scan(other, Set.of("build"))).isEqualTo(DiskUsage.scan(root, Set.of("build")));
    }

    @Test
    void failedFilesAreCollectedAndTheWalkContinues() {
        var usage = new DiskUsage(root, Set.of(), Long.MAX_VALUE);
        FileVisitResult result = usage.visitFileFailed(root.resolve("locked.txt"),
                new AccessDeniedException("locked.txt"));
        assertThat(result).isEqualTo(FileVisitResult.CONTINUE);
        assertThat(usage.report().failures()).containsExactly("locked.txt: AccessDeniedException");
    }

    @Test
    void demoPrintsASortedRelativeReportAndDeletesItsTree() {
        String output = Console.capture(() -> {
            try {
                DiskUsageDemo.main(new String[0]);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        assertThat(output).isEqualTo("""
                extension  files  bytes
                (none)         1     50
                java           2    500
                md             2    200
                txt            1     40
                total          6    790
                skipped:  [build, src/build]
                failures: []
                with a 1-byte budget: stopped early after 1 file(s)
                temporary tree deleted: true
                """);
    }
}
