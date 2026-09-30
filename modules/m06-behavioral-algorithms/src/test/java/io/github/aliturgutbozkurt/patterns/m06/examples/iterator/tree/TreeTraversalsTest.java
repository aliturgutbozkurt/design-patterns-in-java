package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.tree;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class TreeTraversalsTest {

    private static final Node<String> ORG_CHART = Node.of("CEO",
            Node.of("CTO", Node.of("Dev Lead"), Node.of("QA Lead")),
            Node.of("CFO", Node.of("Accountant")));

    private static <T> List<T> toList(Iterable<T> values) {
        var list = new ArrayList<T>();
        values.forEach(list::add);
        return list;
    }

    @Test
    void depthFirstIsPreOrder() {
        assertThat(toList(TreeTraversals.depthFirst(ORG_CHART)))
                .containsExactly("CEO", "CTO", "Dev Lead", "QA Lead", "CFO", "Accountant");
    }

    @Test
    void breadthFirstIsLevelOrder() {
        assertThat(toList(TreeTraversals.breadthFirst(ORG_CHART)))
                .containsExactly("CEO", "CTO", "CFO", "Dev Lead", "QA Lead", "Accountant");
    }

    @Test
    void deepChainIsTraversedWithoutStackOverflow() {
        Node<Integer> chain = Node.of(0);
        for (int i = 1; i < 100_000; i++) {
            chain = Node.of(i, chain);
        }
        long depthFirstCount = 0;
        for (int _ : TreeTraversals.depthFirst(chain)) {
            depthFirstCount++;
        }
        long breadthFirstCount = 0;
        for (int _ : TreeTraversals.breadthFirst(chain)) {
            breadthFirstCount++;
        }
        assertThat(depthFirstCount).isEqualTo(100_000);
        assertThat(breadthFirstCount).isEqualTo(100_000);
    }

    @Test
    void removeIsUnsupportedAndNextPastTheEndThrows() {
        Iterator<String> it = TreeTraversals.depthFirst(Node.of("only")).iterator();
        it.next();
        assertThatThrownBy(it::remove).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(it::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void demoPrintsBothTraversals() {
        assertThat(Console.capture(() -> TreeIteratorDemo.main(new String[0]))).isEqualTo("""
                depth-first:   CEO, CTO, Dev Lead, QA Lead, CFO, Accountant
                breadth-first: CEO, CTO, CFO, Dev Lead, QA Lead, Accountant
                nodes visited in a 100 000-level chain: 100000
                """);
    }
}
