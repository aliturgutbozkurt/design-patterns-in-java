package io.github.aliturgutbozkurt.patterns.m11.examples.repository;

import io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders.ConcurrentUpdateException;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders.InMemoryOrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders.OrderRepository;

/** Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/repository/OptimisticLockingDemo.java} */
public final class OptimisticLockingDemo {

    private OptimisticLockingDemo() {}

    public static void main(String[] args) {
        OrderRepository orders = new InMemoryOrderRepository();
        Order created = new Order(orders.nextId());
        orders.save(created);
        System.out.println("created " + created.id() + " at version " + created.version());

        // Two clerks open the same order.
        Order ada = orders.findById("order-1").orElseThrow();
        Order alan = orders.findById("order-1").orElseThrow();
        System.out.println("ada and alan both load order-1 at version " + ada.version());

        ada.addLine("BOOK-1", 2);
        orders.save(ada);
        System.out.println("ada adds BOOK-1 x 2 and saves: version " + ada.version());

        alan.addLine("PEN-7", 1);
        try {
            orders.save(alan); // would silently overwrite ada's change without the version check
        } catch (ConcurrentUpdateException e) {
            System.out.println("alan adds PEN-7 x 1 and saves: " + e.getMessage());
        }
        Order stored = orders.findById("order-1").orElseThrow();
        System.out.println("stored: " + stored.lines() + " at version " + stored.version());

        Order retry = orders.findById("order-1").orElseThrow();
        retry.addLine("PEN-7", 1);
        orders.save(retry);
        System.out.println("alan reloads, re-applies and saves: " + retry.lines() + " at version " + retry.version());
    }
}
