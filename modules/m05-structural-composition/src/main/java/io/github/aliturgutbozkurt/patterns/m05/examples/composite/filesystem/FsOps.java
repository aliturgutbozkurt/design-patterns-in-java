package io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Operations over the file system tree, each one a recursive exhaustive {@code switch} with record patterns.
 * Adding an operation means adding a method here — the node records do not change.
 *
 * @see "m05 lesson, section Composite — modern Java 27"
 */
public final class FsOps {

    private FsOps() {}

    /** Total bytes in the tree. */
    public static long size(FsNode node) {
        return switch (node) {
            case File(var _, var bytes) -> bytes;
            case Directory(var _, var children) -> children.stream().mapToLong(FsOps::size).sum();
        };
    }

    /** Number of files (directories are not counted). */
    public static int fileCount(FsNode node) {
        return switch (node) {
            case File _ -> 1;
            case Directory(var _, var children) -> children.stream().mapToInt(FsOps::fileCount).sum();
        };
    }

    /** Nodes on the longest path from this node down; a file or an empty directory has depth 1. */
    public static int depth(FsNode node) {
        return switch (node) {
            case File _ -> 1;
            case Directory(var _, var children) -> 1 + children.stream().mapToInt(FsOps::depth).max().orElse(0);
        };
    }

    /** Paths (relative to {@code root}, starting with {@code /}) of matching files, in depth-first order. */
    public static List<String> find(FsNode root, Predicate<File> test) {
        var found = new ArrayList<String>();
        switch (root) {
            case File file -> collect(file, "", test, found);
            case Directory(var _, var children) -> children.forEach(child -> collect(child, "", test, found));
        }
        return List.copyOf(found);
    }

    private static void collect(FsNode node, String parent, Predicate<File> test, List<String> found) {
        String path = parent + "/" + node.name();
        switch (node) {
            case File file when test.test(file) -> found.add(path);
            case File _ -> { }
            case Directory(var _, var children) -> children.forEach(child -> collect(child, path, test, found));
        }
    }

    /** One line per node, two spaces of indent per level; directories end in {@code /} and show their total. */
    public static String render(FsNode node) {
        var text = new StringBuilder();
        render(node, 0, text);
        return text.toString();
    }

    private static void render(FsNode node, int level, StringBuilder text) {
        text.append("  ".repeat(level));
        switch (node) {
            case File(var name, var bytes) -> text.append(name).append(" (").append(bytes).append(" bytes)\n");
            case Directory(var name, var children) -> {
                text.append(name).append("/ (").append(size(node)).append(" bytes)\n");
                children.forEach(child -> render(child, level + 1, text));
            }
        }
    }
}
