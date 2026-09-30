package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns;

import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.after.InMemoryStockLevels;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before.ReorderPolicy;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before.ServiceLocator;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before.StockLevels;

/** Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/antipatterns/GlobalStateDemo.java} */
public final class GlobalStateDemo {

    private GlobalStateDemo() {}

    public static void main(String[] args) {
        try {
            // Scenario 1 sets up its stock through the Singleton and asks the service.
            ServiceLocator.register(StockLevels.class, StockLevels.getInstance());
            ServiceLocator.register(ReorderPolicy.class, new ReorderPolicy(5));
            StockLevels.getInstance().set("PEN-7", 2);
            StockLevels.getInstance().set("BOOK-1", 10);
            var first = new io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before.ReorderService();
            System.out.println("before, scenario 1: reorder " + first.itemsToReorder());

            // Scenario 2 believes it starts fresh: a new service, only MUG-3 in stock.
            StockLevels.getInstance().set("MUG-3", 1);
            var second = new io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before.ReorderService();
            System.out.println("before, scenario 2: reorder " + second.itemsToReorder() + "  <- PEN-7 leaked in");
        } finally {
            ServiceLocator.reset(); // global state must be cleaned up by hand
        }

        var shopA = new io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.after.ReorderService(
                new InMemoryStockLevels().set("PEN-7", 2).set("BOOK-1", 10), () -> 5);
        var shopB = new io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.after.ReorderService(
                new InMemoryStockLevels().set("MUG-3", 1), () -> 5);
        System.out.println("after, scenario 1: reorder " + shopA.itemsToReorder());
        System.out.println("after, scenario 2: reorder " + shopB.itemsToReorder());
    }
}
