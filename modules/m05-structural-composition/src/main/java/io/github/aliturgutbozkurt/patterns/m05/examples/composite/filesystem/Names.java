package io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem;

/** Name rule shared by files and directories (package-private helper). */
final class Names {

    private Names() {}

    static void check(String name) {
        if (name == null || name.isBlank() || name.contains("/")) {
            throw new IllegalArgumentException("invalid name: " + name);
        }
    }
}
