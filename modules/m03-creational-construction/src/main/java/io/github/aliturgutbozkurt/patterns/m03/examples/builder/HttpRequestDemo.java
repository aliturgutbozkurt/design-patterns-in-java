package io.github.aliturgutbozkurt.patterns.m03.examples.builder;

import io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic.HttpRequest;
import io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic.HttpRequest.Method;
import java.net.URI;
import java.time.Duration;

/** Run: {@code java modules/m03-creational-construction/src/main/java/io/github/aliturgutbozkurt/patterns/m03/examples/builder/HttpRequestDemo.java} */
public final class HttpRequestDemo {

    private HttpRequestDemo() {}

    public static void main(String[] args) {
        URI orders = URI.create("https://api.example.com/orders");
        System.out.print(HttpRequest.newBuilder(orders).build().describe());

        HttpRequest create = HttpRequest.newBuilder(orders)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .method(Method.POST, "{\"item\":\"pen\",\"quantity\":2}")
                .timeout(Duration.ofSeconds(5))
                .build();
        System.out.print(create.describe());

        try {
            HttpRequest.newBuilder(orders).method(Method.GET, "oops").build();
        } catch (IllegalStateException e) {
            System.out.println("invalid request: " + e.getMessage());
        }
    }
}
