package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.tuple;

import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest.Forest;
import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest.Tree;
import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest.TreeType;
import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest.TreeTypeFactory;
import io.github.aliturgutbozkurt.patterns.m05.support.Console;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.junit.jupiter.api.Test;

class ForestTest {

    @Test
    void hundredThousandTreesShareThreeTreeTypeInstances() {
        var types = new TreeTypeFactory();
        var forest = new Forest(types::typeOf);
        forest.plantGrid(400, 250);
        assertThat(forest.treeCount()).isEqualTo(100_000);
        assertThat(forest.distinctTypeInstances()).isEqualTo(3);
        assertThat(types.created()).isEqualTo(3);
    }

    @Test
    void naiveForestHasOneTreeTypePerTree() {
        var forest = new Forest(TreeType::new);
        forest.plantGrid(400, 250);
        assertThat(forest.treeCount()).isEqualTo(100_000);
        assertThat(forest.distinctTypeInstances()).isEqualTo(100_000);
    }

    @Test
    void factoryReturnsTheSameInstanceForEqualIntrinsicState() {
        var types = new TreeTypeFactory();
        TreeType oak = types.typeOf("oak", "green", "rough bark");
        assertThat(types.typeOf("oak", "green", "rough bark")).isSameAs(oak);
        assertThat(types.typeOf("oak", "red", "rough bark")).isNotSameAs(oak);
    }

    @Test
    void regionListsTreesInRowThenColumnOrder() {
        var types = new TreeTypeFactory();
        var forest = new Forest(types::typeOf);
        forest.plant(2, 1, "oak", "green", "rough bark");
        forest.plant(0, 1, "pine", "dark green", "needles");
        forest.plant(5, 0, "birch", "white", "smooth bark");
        forest.plant(9, 9, "oak", "green", "rough bark");
        assertThat(forest.region(0, 0, 5, 1)).extracting(Tree::x, Tree::y)
                .containsExactly(tuple(5, 0), tuple(0, 1), tuple(2, 1));
    }

    @Test
    void rejectsInvalidInput() {
        assertThatIllegalArgumentException().isThrownBy(() -> new TreeType(" ", "green", "bark"));
        assertThatIllegalArgumentException().isThrownBy(() -> new Forest(TreeType::new).plantGrid(0, 5));
    }

    @Test
    void demoPrintsTreeCountsAgainstDistinctTypeInstances() {
        assertThat(Console.capture(() -> {
            try {
                ForestDemo.main(new String[0]);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        })).isEqualTo("""
                shared forest: 100000 trees, 3 distinct TreeType instances
                naive forest:  100000 trees, 100000 distinct TreeType instances
                region x 0..3, y 0..1:
                  (0,0) oak
                  (1,0) pine
                  (2,0) birch
                  (3,0) oak
                  (0,1) birch
                  (1,1) oak
                  (2,1) pine
                  (3,1) birch
                """);
    }
}
