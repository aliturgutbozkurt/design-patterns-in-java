package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.tree;

import java.util.StringJoiner;

/**
 * Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/iterator/tree/TreeIteratorDemo.java}
 *
 * @see "m06 lesson, section Iterator"
 */
public final class TreeIteratorDemo {

    private TreeIteratorDemo() {}

    public static void main(String[] args) {
        Node<String> orgChart = Node.of("CEO",
                Node.of("CTO", Node.of("Dev Lead"), Node.of("QA Lead")),
                Node.of("CFO", Node.of("Accountant")));

        System.out.println("depth-first:   " + join(TreeTraversals.depthFirst(orgChart)));
        System.out.println("breadth-first: " + join(TreeTraversals.breadthFirst(orgChart)));

        Node<Integer> chain = Node.of(0);
        for (int i = 1; i < 100_000; i++) {
            chain = Node.of(i, chain);  // 100 000 levels deep: recursion would overflow the stack
        }
        int visited = 0;
        for (int _ : TreeTraversals.depthFirst(chain)) {
            visited++;
        }
        System.out.println("nodes visited in a 100 000-level chain: " + visited);
    }

    private static String join(Iterable<String> values) {
        var joiner = new StringJoiner(", ");
        values.forEach(joiner::add);
        return joiner.toString();
    }
}
