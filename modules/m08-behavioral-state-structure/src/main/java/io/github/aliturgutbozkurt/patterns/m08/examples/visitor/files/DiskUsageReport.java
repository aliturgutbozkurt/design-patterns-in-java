package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.files;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * What {@link DiskUsage} found: files and bytes per extension (sorted), skipped directories and failures as sorted
 * paths relative to the root, and whether a byte budget stopped the walk.
 *
 * @see "m08 lesson, section Visitor — Real-world usage"
 */
public record DiskUsageReport(SortedMap<String, Usage> byExtension, List<String> skippedDirectories,
        List<String> failures, boolean stoppedEarly) {

    /** Files and bytes for one extension. */
    public record Usage(int files, long bytes) {

        public Usage plus(Usage other) {
            return new Usage(files + other.files, Math.addExact(bytes, other.bytes));
        }
    }

    public DiskUsageReport {
        byExtension = Collections.unmodifiableSortedMap(new TreeMap<>(byExtension));
        skippedDirectories = List.copyOf(skippedDirectories);
        failures = List.copyOf(failures);
    }

    public int totalFiles() {
        return byExtension.values().stream().mapToInt(Usage::files).sum();
    }

    public long totalBytes() {
        return byExtension.values().stream().mapToLong(Usage::bytes).sum();
    }
}
