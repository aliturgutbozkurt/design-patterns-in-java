package io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem;

/**
 * Composite leaf: a file with a name and a size in bytes.
 *
 * @see "m05 lesson, section Composite — modern Java 27"
 */
public record File(String name, long bytes) implements FsNode {

    public File {
        Names.check(name);
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes must not be negative: " + bytes);
        }
    }
}
