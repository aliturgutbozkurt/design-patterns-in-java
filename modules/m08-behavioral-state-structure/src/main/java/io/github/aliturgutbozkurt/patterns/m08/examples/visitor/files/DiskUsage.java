package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.files;

import io.github.aliturgutbozkurt.patterns.m08.examples.visitor.files.DiskUsageReport.Usage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The JDK's classic Visitor: {@code Files.walkFileTree} calls one method per event, and the returned
 * {@link FileVisitResult} steers the walk ({@code SKIP_SUBTREE} for ignored directories, {@code TERMINATE} once the
 * byte budget is used up). Everything collected is sorted, so the report does not depend on iteration order.
 *
 * @see "m08 lesson, section Visitor — Real-world usage"
 */
public final class DiskUsage extends SimpleFileVisitor<Path> {

    private final Path root;
    private final Set<String> ignoredDirectories;
    private final long byteBudget;
    private final SortedMap<String, Usage> byExtension = new TreeMap<>();
    private final SortedSet<String> skipped = new TreeSet<>();
    private final SortedSet<String> failures = new TreeSet<>();
    private long bytesSoFar;
    private boolean stoppedEarly;

    /** A visitor for one walk from {@code root}; {@code byteBudget} is {@code Long.MAX_VALUE} for "no limit". */
    public DiskUsage(Path root, Set<String> ignoredDirectories, long byteBudget) {
        this.root = Objects.requireNonNull(root, "root");
        this.ignoredDirectories = Set.copyOf(ignoredDirectories);
        if (byteBudget <= 0) {
            throw new IllegalArgumentException("byte budget must be positive: " + byteBudget);
        }
        this.byteBudget = byteBudget;
    }

    public static DiskUsageReport scan(Path root, Set<String> ignoredDirectories) throws IOException {
        return scan(root, ignoredDirectories, Long.MAX_VALUE);
    }

    public static DiskUsageReport scan(Path root, Set<String> ignoredDirectories, long byteBudget)
            throws IOException {
        var usage = new DiskUsage(root, ignoredDirectories, byteBudget);
        Files.walkFileTree(root, usage);
        return usage.report();
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) {
        if (!dir.equals(root) && ignoredDirectories.contains(dir.getFileName().toString())) {
            skipped.add(relative(dir));
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
        byExtension.merge(extension(file), new Usage(1, attributes.size()), Usage::plus);
        bytesSoFar = Math.addExact(bytesSoFar, attributes.size());
        if (bytesSoFar >= byteBudget) {
            stoppedEarly = true;
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

    /** A file that cannot be read is recorded, not fatal: the walk goes on. */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException failure) {
        failures.add(relative(file) + ": " + failure.getClass().getSimpleName());
        return FileVisitResult.CONTINUE;
    }

    public DiskUsageReport report() {
        return new DiskUsageReport(byExtension, List.copyOf(skipped), List.copyOf(failures), stoppedEarly);
    }

    private String relative(Path path) {
        return root.relativize(path).toString().replace(File.separatorChar, '/');
    }

    private static String extension(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot <= 0 ? "(none)" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
