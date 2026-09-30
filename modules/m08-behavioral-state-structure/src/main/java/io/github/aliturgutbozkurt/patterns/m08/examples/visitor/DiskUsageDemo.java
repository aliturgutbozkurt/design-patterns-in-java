package io.github.aliturgutbozkurt.patterns.m08.examples.visitor;

import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.files.DiskUsage;
import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.files.DiskUsageReport;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Run: {@code java modules/m08-behavioral-state-structure/src/main/java/io/github/aliturgutbozkurt/patterns/m08/examples/visitor/DiskUsageDemo.java} */
public final class DiskUsageDemo {

    private DiskUsageDemo() {}

    /** Writes the fixed sample tree (file → size in bytes) under {@code root}. */
    public static void createSampleTree(Path root) throws IOException {
        Map<String, Integer> files = Map.of(
                "README.md", 80, "LICENSE", 50,
                "docs/guide.md", 120, "docs/notes.txt", 40,
                "src/App.java", 300, "src/Util.java", 200,
                "src/build/App.class", 999, "build/app.jar", 5000);
        for (var file : files.entrySet()) {
            Path path = root.resolve(file.getKey());
            Files.createDirectories(path.getParent());
            Files.writeString(path, "x".repeat(file.getValue()));
        }
    }

    public static void main(String[] args) throws IOException {
        Path root = Files.createTempDirectory("m08-disk-usage");
        try {
            createSampleTree(root);
            DiskUsageReport report = DiskUsage.scan(root, Set.of("build"));
            System.out.println("extension  files  bytes");
            report.byExtension().forEach((extension, usage) -> row(extension, usage.files(), usage.bytes()));
            row("total", report.totalFiles(), report.totalBytes());
            System.out.println("skipped:  " + report.skippedDirectories());
            System.out.println("failures: " + report.failures());
            DiskUsageReport budget = DiskUsage.scan(root, Set.of("build"), 1);
            System.out.println("with a 1-byte budget: stopped early after " + budget.totalFiles() + " file(s)");
        } finally {
            deleteTree(root);
        }
        System.out.println("temporary tree deleted: " + Files.notExists(root));
    }

    private static void row(String extension, int files, long bytes) {
        System.out.print(String.format(Locale.ROOT, "%-10s %5d %6d\n", extension, files, bytes));
    }

    /** Another FileVisitor: delete files on the way down and each directory after its contents. */
    private static void deleteTree(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException failure) throws IOException {
                if (failure != null) {
                    throw failure;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
