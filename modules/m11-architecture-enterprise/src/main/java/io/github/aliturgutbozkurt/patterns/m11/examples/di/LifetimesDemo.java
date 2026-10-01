package io.github.aliturgutbozkurt.patterns.m11.examples.di;

import io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes.Basket;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes.PriceList;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes.RequestLog;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes.RequestScope;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes.ShopCompositionRoot;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/** Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/di/LifetimesDemo.java} */
public final class LifetimesDemo {

    private LifetimesDemo() {}

    public static void main(String[] args) {
        Clock clock = Clock.fixed(Instant.parse("2026-09-30T10:00:00Z"), ZoneOffset.UTC);
        try (var root = ShopCompositionRoot.production(clock, line -> System.out.println("  disk> " + line))) {
            Basket firstBasket;
            PriceList firstPrices;
            try (RequestScope request = root.beginRequest()) {
                firstBasket = request.basket();
                firstPrices = request.priceList();
                request.basket().add("book");
                request.basket().add("pen");
                System.out.println("request 1: same basket within the request: " + (request.basket() == firstBasket));
                request.checkout();
            }
            try (RequestScope request = root.beginRequest()) {
                request.basket().add("mug");
                System.out.println("request 2: new basket: " + (request.basket() != firstBasket));
                System.out.println("request 2: same price list: " + (request.priceList() == firstPrices));
                request.checkout();
            }
            RequestLog first = root.requestLog();
            RequestLog second = root.requestLog();
            first.record("GET /basket");
            second.record("POST /checkout");
            System.out.println("transient logs are different objects: " + (first != second));
            System.out.println("closing the root:");
        }
    }
}
