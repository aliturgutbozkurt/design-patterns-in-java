package io.github.aliturgutbozkurt.patterns.m01.examples.composition;

import io.github.aliturgutbozkurt.patterns.m01.examples.composition.after.CountingSet;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/** Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/composition/CountingSetDemo.java} */
public final class CountingSetDemo {

    private CountingSetDemo() {}

    public static void main(String[] args) {
        var fruit = List.of("pear", "apple", "fig");

        System.out.println("== before: CountingSet extends HashSet ==");
        var inherited = new io.github.aliturgutbozkurt.patterns.m01.examples.composition.before.CountingSet<String>();
        inherited.addAll(fruit);
        System.out.println("addAll of 3 elements -> addCount " + inherited.addCount()
                + " (HashSet.addAll calls our overridden add)");

        System.out.println("== after: CountingSet wraps any Set ==");
        var composed = new CountingSet<>(new HashSet<String>());
        composed.addAll(fruit);
        System.out.println("addAll of 3 elements -> addCount " + composed.addCount());
        var sorted = new CountingSet<>(new TreeSet<String>());
        sorted.addAll(fruit);
        System.out.println("wrapping a TreeSet keeps its order: " + sorted);
    }
}
