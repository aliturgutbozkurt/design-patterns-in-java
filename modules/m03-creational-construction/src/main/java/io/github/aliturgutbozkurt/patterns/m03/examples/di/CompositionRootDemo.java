package io.github.aliturgutbozkurt.patterns.m03.examples.di;

import java.util.List;

/**
 * Run: {@code java modules/m03-creational-construction/src/main/java/io/github/aliturgutbozkurt/patterns/m03/examples/di/CompositionRootDemo.java}
 *
 * @see "m03 lesson, section Dependency injection as creation"
 */
public final class CompositionRootDemo {

    private CompositionRootDemo() {}

    public static void main(String[] args) {
        ShopApp app = CompositionRoot.production();  // the whole object graph is built here, once
        List<OrderRecord> orders = List.of(
                app.checkout().checkout("ada", "keyboard", 2),
                app.checkout().checkout("alan", "monitor", 1));
        for (OrderRecord order : orders) {
            System.out.println(order.customer() + " bought " + order.quantity() + " x " + order.item()
                    + " for " + order.total() + " (receipt " + order.receipt() + ")");
        }
        System.out.println("orders stored: " + app.orders().all().size());
    }
}
