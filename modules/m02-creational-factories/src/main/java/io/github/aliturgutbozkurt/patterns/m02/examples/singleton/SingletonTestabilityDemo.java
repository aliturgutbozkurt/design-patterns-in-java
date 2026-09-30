package io.github.aliturgutbozkurt.patterns.m02.examples.singleton;

import io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.after.OrderService;
import io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.after.SequentialIds;

/** Run: {@code java modules/m02-creational-factories/src/main/java/io/github/aliturgutbozkurt/patterns/m02/examples/singleton/SingletonTestabilityDemo.java} */
public final class SingletonTestabilityDemo {

    private SingletonTestabilityDemo() {}

    public static void main(String[] args) {
        System.out.println("== before: OrderService calls SequenceGenerator.getInstance() ==");
        var first = new io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.before.OrderService();
        var second = new io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.before.OrderService();
        int a = number(first.placeOrder());
        int b = number(second.placeOrder());
        System.out.println("a second OrderService continues the first one's numbers: " + (b == a + 1));

        System.out.println("== after: the IdSource is injected ==");
        var shared = new SequentialIds();  // the composition root decides: one shared instance
        System.out.println("shared on purpose: " + new OrderService(shared).placeOrder()
                + ", " + new OrderService(shared).placeOrder());
        System.out.println("separate sources: " + new OrderService(new SequentialIds()).placeOrder()
                + ", " + new OrderService(new SequentialIds()).placeOrder());
        System.out.println("fixed for a test: " + new OrderService(() -> 42).placeOrder());
    }

    private static int number(String orderNumber) {
        return Integer.parseInt(orderNumber.substring("ORD-".length()));
    }
}
