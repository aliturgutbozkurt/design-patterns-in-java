package io.github.aliturgutbozkurt.patterns.m05.examples.composite;

import io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem.Directory;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem.File;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem.FsOps;

/**
 * Run: {@code java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/composite/FileSystemDemo.java}
 *
 * @see "m05 lesson, section Composite"
 */
public final class FileSystemDemo {

    private FileSystemDemo() {}

    public static void main(String[] args) {
        var project = Directory.of("project",
                new File("README.md", 300),
                Directory.of("src",
                        Directory.of("main", new File("App.java", 1200), new File("Util.java", 3000)),
                        Directory.of("test", new File("AppTest.java", 800))),
                Directory.of("docs"));

        System.out.print(FsOps.render(project));
        System.out.println("files: " + FsOps.fileCount(project) + ", total: " + FsOps.size(project)
                + " bytes, depth: " + FsOps.depth(project));
        System.out.println("java sources: " + FsOps.find(project, file -> file.name().endsWith(".java")));
        System.out.println("larger than 1000 bytes: " + FsOps.find(project, file -> file.bytes() > 1000));
        try {
            Directory.of("src", Directory.of("main"), Directory.of("main"));
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }
    }
}
