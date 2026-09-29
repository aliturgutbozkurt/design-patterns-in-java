package io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem;

import java.util.HashSet;
import java.util.List;

/**
 * Composite node: a directory holds an immutable copy of its children, so a tree can be shared freely.
 *
 * @see "m05 lesson, section Composite — modern Java 27"
 */
public record Directory(String name, List<FsNode> children) implements FsNode {

    public Directory {
        Names.check(name);
        children = List.copyOf(children);
        var seen = new HashSet<String>();
        for (FsNode child : children) {
            if (!seen.add(child.name())) {
                throw new IllegalArgumentException("duplicate name in " + name + ": " + child.name());
            }
        }
    }

    public static Directory of(String name, FsNode... children) {
        return new Directory(name, List.of(children));
    }
}
