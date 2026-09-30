package io.github.aliturgutbozkurt.patterns.m01.examples.composition;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m01.examples.composition.after.CountingSet;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class CountingSetTest {

    /** Documents the fragile base class on purpose: HashSet.addAll calls our overridden add. */
    @Test
    void beforeSubclassCountsAddAllTwice() {
        var set = new io.github.aliturgutbozkurt.patterns.m01.examples.composition.before.CountingSet<String>();
        set.addAll(List.of("a", "b", "c"));
        assertThat(set.addCount()).isEqualTo(6);
    }

    @Test
    void afterCountsAddAndAddAllExactly() {
        var set = new CountingSet<>(new HashSet<String>());
        set.addAll(List.of("a", "b", "c"));
        set.add("d");
        assertThat(set.addCount()).isEqualTo(4);
        assertThat(set).containsExactlyInAnyOrder("a", "b", "c", "d");
    }

    @Test
    void afterWrapsAnySetAndKeepsItsBehaviour() {
        var sorted = new CountingSet<>(new TreeSet<String>());
        sorted.addAll(List.of("pear", "apple", "fig"));
        assertThat(sorted).containsExactly("apple", "fig", "pear");
        assertThat(sorted.addCount()).isEqualTo(3);
    }

    @Test
    void afterKeepsTheSetContractForEqualsAndHashCode() {
        var counting = new CountingSet<>(new HashSet<String>());
        counting.addAll(List.of("a", "b"));
        Set<String> plain = Set.of("a", "b");
        assertThat(counting).isEqualTo(plain).hasSameHashCodeAs(plain);
        assertThat(plain).isEqualTo(counting);
        assertThat(counting.remove("a")).isTrue();
        assertThat(counting).containsExactly("b");
    }

    @Test
    void demoShowsTheOverCountAndTheFix() {
        assertThat(Console.capture(() -> CountingSetDemo.main(new String[0]))).isEqualTo("""
                == before: CountingSet extends HashSet ==
                addAll of 3 elements -> addCount 6 (HashSet.addAll calls our overridden add)
                == after: CountingSet wraps any Set ==
                addAll of 3 elements -> addCount 3
                wrapping a TreeSet keeps its order: [apple, fig, pear]
                """);
    }
}
