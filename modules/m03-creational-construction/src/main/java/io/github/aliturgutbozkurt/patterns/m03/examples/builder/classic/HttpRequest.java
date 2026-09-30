package io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.regex.Pattern;

/**
 * An immutable HTTP request built with a Builder — the same shape as {@code java.net.http.HttpRequest.newBuilder()}.
 * Individual parts are checked as they are added; rules that involve several parts are checked in {@code build()}.
 *
 * @see "m03 lesson, section Builder"
 */
public final class HttpRequest {

    /** HTTP method. */
    public enum Method { GET, POST, PUT, DELETE }

    private static final Pattern HEADER_NAME = Pattern.compile("[A-Za-z0-9-]+");
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    private final URI uri;
    private final Method method;
    private final SequencedMap<String, String> headers;
    private final String body;
    private final Duration timeout;

    private HttpRequest(Builder builder) {
        uri = builder.uri;
        method = builder.method;
        headers = Collections.unmodifiableSequencedMap(new LinkedHashMap<>(builder.headers));
        body = builder.body;
        timeout = builder.timeout;
    }

    public static Builder newBuilder(URI uri) {
        return new Builder(uri);
    }

    public URI uri() {
        return uri;
    }

    public Method method() {
        return method;
    }

    /** Headers in the order they were added; unmodifiable. */
    public SequencedMap<String, String> headers() {
        return headers;
    }

    public Optional<String> body() {
        return Optional.ofNullable(body);
    }

    public Duration timeout() {
        return timeout;
    }

    /** Request line, headers and body, as they would go over the wire (simplified). */
    public String describe() {
        var text = new StringBuilder(method + " " + uri + " (timeout " + timeout + ")\n");
        headers.forEach((name, value) -> text.append(name).append(": ").append(value).append('\n'));
        body().ifPresent(content -> text.append('\n').append(content).append('\n'));
        return text.toString();
    }

    /**
     * Mutable builder; defaults are GET and a 30-second timeout.
     *
     * @see "m03 lesson, section Builder"
     */
    public static final class Builder {

        private final URI uri;
        private Method method = Method.GET;
        private final Map<String, String> headers = new LinkedHashMap<>();
        private String body;
        private Duration timeout = DEFAULT_TIMEOUT;

        private Builder(URI uri) {
            this.uri = Objects.requireNonNull(uri, "uri");
        }

        /** Adds a header; names are letters, digits and dashes, and unique ignoring case. */
        public Builder header(String name, String value) {
            if (!HEADER_NAME.matcher(name).matches()) {
                throw new IllegalArgumentException("invalid header name: '" + name + "'");
            }
            boolean duplicate = headers.keySet().stream().anyMatch(existing -> existing.equalsIgnoreCase(name));
            if (duplicate) {
                throw new IllegalArgumentException("duplicate header: " + name);
            }
            headers.put(name, Objects.requireNonNull(value, "value"));
            return this;
        }

        /** Sets the method and body ({@code null} for no body). */
        public Builder method(Method method, String body) {
            this.method = Objects.requireNonNull(method, "method");
            this.body = body;
            return this;
        }

        public Builder timeout(Duration timeout) {
            if (timeout.isNegative() || timeout.isZero()) {
                throw new IllegalArgumentException("timeout must be positive: " + timeout);
            }
            this.timeout = timeout;
            return this;
        }

        /** Checks the rules that involve several parts, then creates the request. */
        public HttpRequest build() {
            boolean hasBody = body != null;
            switch (method) {
                case GET, DELETE -> {
                    if (hasBody) {
                        throw new IllegalStateException(method + " request must not have a body");
                    }
                }
                case POST, PUT -> {
                    if (!hasBody) {
                        throw new IllegalStateException(method + " request needs a body");
                    }
                }
            }
            return new HttpRequest(this);
        }
    }
}
