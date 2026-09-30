package io.github.aliturgutbozkurt.patterns.m07.examples.chain;

import io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware.Handler;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware.Middlewares;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware.Pipeline;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware.Request;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/** Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/chain/MiddlewareDemo.java} */
public final class MiddlewareDemo {

    private MiddlewareDemo() {}

    public static void main(String[] args) {
        List<String> log = new ArrayList<>();
        var ids = new AtomicInteger();
        Handler endpoint = request -> switch (request.path()) {
            case "/orders" -> new Response(200, "orders for " + request.header(Middlewares.REQUEST_ID).orElseThrow());
            case "/crash" -> throw new IllegalStateException("database down");
            default -> new Response(404, "no route for " + request.path());
        };
        Handler server = Pipeline.of(List.of(
                Middlewares.logging(log),
                Middlewares.errorBoundary(),
                Middlewares.authentication(Set.of("s3cret")),
                Middlewares.requestId(() -> "req-" + ids.incrementAndGet())), endpoint);

        List<Request> requests = List.of(
                new Request("GET", "/orders").withHeader("Authorization", "Bearer s3cret"),
                new Request("GET", "/orders"),
                new Request("GET", "/crash").withHeader("Authorization", "Bearer s3cret"));
        for (Request request : requests) {
            Response response = server.handle(request);
            log.forEach(System.out::println);
            log.clear();
            System.out.println("   " + response.status() + " " + response.body());
        }
    }
}
