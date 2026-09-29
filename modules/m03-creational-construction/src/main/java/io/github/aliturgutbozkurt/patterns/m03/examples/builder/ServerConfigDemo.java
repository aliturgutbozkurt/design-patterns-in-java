package io.github.aliturgutbozkurt.patterns.m03.examples.builder;

import io.github.aliturgutbozkurt.patterns.m03.examples.builder.record.ServerConfig;
import java.time.Duration;

/** Run: {@code java modules/m03-creational-construction/src/main/java/io/github/aliturgutbozkurt/patterns/m03/examples/builder/ServerConfigDemo.java} */
public final class ServerConfigDemo {

    private ServerConfigDemo() {}

    public static void main(String[] args) {
        System.out.println("defaults: " + ServerConfig.builder("localhost").build());
        ServerConfig production = ServerConfig.builder("api.example.com")
                .port(443)
                .tls(true)
                .timeout(Duration.ofSeconds(10))
                .maxConnections(500)
                .build();
        System.out.println("production: " + production);
        System.out.println("staging copy: " + production.withPort(8443));
    }
}
