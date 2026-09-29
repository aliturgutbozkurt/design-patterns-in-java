package io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable HTTP-style request.
 *
 * @see "m07 lesson, section Chain of Responsibility — middleware"
 */
public record Request(String method, String path, Map<String, String> headers) {

    public Request {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(path, "path");
        headers = Map.copyOf(headers);
    }

    /** A request without headers. */
    public Request(String method, String path) {
        this(method, path, Map.of());
    }

    public Optional<String> header(String name) {
        return Optional.ofNullable(headers.get(name));
    }

    /** A copy with one header added or replaced. */
    public Request withHeader(String name, String value) {
        var copy = new HashMap<>(headers);
        copy.put(Objects.requireNonNull(name, "name"), Objects.requireNonNull(value, "value"));
        return new Request(method, path, copy);
    }
}
