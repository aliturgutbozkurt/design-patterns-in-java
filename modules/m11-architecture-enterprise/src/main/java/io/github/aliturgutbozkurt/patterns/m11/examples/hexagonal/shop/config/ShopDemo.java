package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.config;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.examples.support.TempDirectory;
import java.nio.file.Path;
import java.util.List;

/** Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/hexagonal/shop/config/ShopDemo.java} */
public final class ShopDemo {

    private static final List<String> SESSION = List.of(
            "place alice BOOK-1:2 PEN-7:1",
            "place bob TOY-9:1",
            "place carol BOOK-1:30",
            "place dave");

    private ShopDemo() {}

    public static void main(String[] args) {
        System.out.println("== in memory");
        run(ShopCompositionRoot.inMemory());

        try (TempDirectory temp = TempDirectory.create("m11-shop")) {
            Path ordersFile = temp.path().resolve("orders.txt");
            System.out.println("== file backed");
            run(ShopCompositionRoot.fileBacked(ordersFile));

            ShopCompositionRoot restarted = ShopCompositionRoot.fileBacked(ordersFile); // same file, new objects
            Order stored = restarted.orders().findById(new OrderId("order-1")).orElseThrow();
            System.out.println("after a restart: " + stored.id() + " for " + stored.customer() + ", total " + stored.total());
            System.out.println("> place erin MUG-3:1");
            System.out.println(restarted.cli().handle("place erin MUG-3:1"));
        }
    }

    private static void run(ShopCompositionRoot shop) {
        for (String line : SESSION) {
            System.out.println("> " + line);
            System.out.println(shop.cli().handle(line));
        }
        System.out.println("payment calls: " + shop.acme().charges() + ", orders saved: " + shop.orders().count());
        System.out.println("published: " + shop.events().published());
    }
}
