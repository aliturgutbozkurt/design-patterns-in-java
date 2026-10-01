package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight;

import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest.Forest;
import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest.Tree;
import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest.TreeType;
import io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest.TreeTypeFactory;
import java.io.IOException;

/**
 * Run: {@code java modules/m05-structural-composition/src/main/java/io/github/aliturgutbozkurt/patterns/m05/examples/flyweight/ForestDemo.java}
 *
 * <p>Manual memory measurement (not part of the tests): add {@code shared} or {@code naive} as an argument; the demo
 * then builds only that forest and waits for Enter, so you can run {@code jcmd <pid> GC.class_histogram}.
 *
 * @see "m05 lesson, section Flyweight"
 */
public final class ForestDemo {

    private ForestDemo() {}

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            holdForMeasurement(args[0]);
            return;
        }
        var types = new TreeTypeFactory();
        var shared = new Forest(types::typeOf);
        shared.plantGrid(400, 250);
        var naive = new Forest(TreeType::new);
        naive.plantGrid(400, 250);

        System.out.println("shared forest: " + shared.treeCount() + " trees, "
                + shared.distinctTypeInstances() + " distinct TreeType instances");
        System.out.println("naive forest:  " + naive.treeCount() + " trees, "
                + naive.distinctTypeInstances() + " distinct TreeType instances");
        System.out.println("region x 0..3, y 0..1:");
        for (Tree tree : shared.region(0, 0, 3, 1)) {
            System.out.println("  (" + tree.x() + "," + tree.y() + ") " + tree.type().species());
        }
    }

    private static void holdForMeasurement(String mode) throws IOException {
        Forest forest = switch (mode) {
            case "shared" -> new Forest(new TreeTypeFactory()::typeOf);
            case "naive" -> new Forest(TreeType::new);
            default -> throw new IllegalArgumentException("mode must be shared or naive: " + mode);
        };
        forest.plantGrid(400, 250);
        System.out.println(mode + " forest with " + forest.treeCount() + " trees is ready; pid "
                + ProcessHandle.current().pid() + ". Run: jcmd <pid> GC.class_histogram — then press Enter.");
        System.in.read();
        System.out.println("still holding " + forest.treeCount() + " trees");   // keeps the forest reachable
    }
}
