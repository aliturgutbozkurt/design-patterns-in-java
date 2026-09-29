package io.github.aliturgutbozkurt.patterns.m05.examples.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem.Directory;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem.File;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem.FsNode;
import io.github.aliturgutbozkurt.patterns.m05.examples.composite.filesystem.FsOps;
import io.github.aliturgutbozkurt.patterns.m05.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class FileSystemTest {

    private final Directory project = Directory.of("project",
            new File("README.md", 300),
            Directory.of("src",
                    Directory.of("main", new File("App.java", 1200), new File("Util.java", 3000)),
                    Directory.of("test", new File("AppTest.java", 800))),
            Directory.of("docs"));

    @Test
    void sizeAndFileCountCoverNestedDirectories() {
        assertThat(FsOps.size(project)).isEqualTo(5300);
        assertThat(FsOps.fileCount(project)).isEqualTo(4);
    }

    @Test
    void aSingleFileIsATreeToo() {
        FsNode file = new File("notes.txt", 42);
        assertThat(FsOps.size(file)).isEqualTo(42);
        assertThat(FsOps.fileCount(file)).isEqualTo(1);
        assertThat(FsOps.depth(file)).isEqualTo(1);
    }

    @Test
    void emptyDirectoryHasSizeZero() {
        assertThat(FsOps.size(Directory.of("empty"))).isZero();
        assertThat(FsOps.fileCount(Directory.of("empty"))).isZero();
        assertThat(FsOps.depth(Directory.of("empty"))).isEqualTo(1);
    }

    @Test
    void depthCountsNodesOnTheLongestPath() {
        assertThat(FsOps.depth(project)).isEqualTo(4);
    }

    @Test
    void findReturnsPathsInDepthFirstOrder() {
        assertThat(FsOps.find(project, file -> file.name().endsWith(".java")))
                .containsExactly("/src/main/App.java", "/src/main/Util.java", "/src/test/AppTest.java");
        assertThat(FsOps.find(project, file -> file.bytes() >= 1000))
                .containsExactly("/src/main/App.java", "/src/main/Util.java");
        assertThat(FsOps.find(project, file -> false)).isEmpty();
    }

    @Test
    void renderGivesTheExactTreeText() {
        assertThat(FsOps.render(project)).isEqualTo("""
                project/ (5300 bytes)
                  README.md (300 bytes)
                  src/ (5000 bytes)
                    main/ (4200 bytes)
                      App.java (1200 bytes)
                      Util.java (3000 bytes)
                    test/ (800 bytes)
                      AppTest.java (800 bytes)
                  docs/ (0 bytes)
                """);
    }

    @Test
    void directoryChildrenAreAnImmutableCopy() {
        List<FsNode> children = new ArrayList<>(List.of(new File("a.txt", 1)));
        var directory = new Directory("dir", children);
        children.add(new File("b.txt", 2));
        assertThat(directory.children()).hasSize(1);
        assertThatThrownBy(() -> directory.children().add(new File("c.txt", 3)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejectsDuplicateNamesNegativeSizesAndBadNames() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Directory.of("dir", new File("a.txt", 1), Directory.of("a.txt")))
                .withMessage("duplicate name in dir: a.txt");
        assertThatIllegalArgumentException().isThrownBy(() -> new File("a.txt", -1))
                .withMessage("bytes must not be negative: -1");
        assertThatIllegalArgumentException().isThrownBy(() -> new File("a/b", 1));
        assertThatIllegalArgumentException().isThrownBy(() -> Directory.of(" "));
    }

    @Test
    void deepTreesNeedNoSpecialCases() {
        FsNode node = new File("leaf.txt", 7);
        for (int i = 0; i < 500; i++) {
            node = Directory.of("d" + i, node);
        }
        assertThat(FsOps.size(node)).isEqualTo(7);
        assertThat(FsOps.depth(node)).isEqualTo(501);
    }

    @Test
    void demoPrintsTreeTotalsAndSearchResults() {
        assertThat(Console.capture(() -> FileSystemDemo.main(new String[0]))).isEqualTo("""
                project/ (5300 bytes)
                  README.md (300 bytes)
                  src/ (5000 bytes)
                    main/ (4200 bytes)
                      App.java (1200 bytes)
                      Util.java (3000 bytes)
                    test/ (800 bytes)
                      AppTest.java (800 bytes)
                  docs/ (0 bytes)
                files: 4, total: 5300 bytes, depth: 4
                java sources: [/src/main/App.java, /src/main/Util.java, /src/test/AppTest.java]
                larger than 1000 bytes: [/src/main/App.java, /src/main/Util.java]
                rejected: duplicate name in src: main
                """);
    }
}
