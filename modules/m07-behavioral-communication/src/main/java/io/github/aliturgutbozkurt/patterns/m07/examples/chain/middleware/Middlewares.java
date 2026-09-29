package io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Ready-made middlewares; each one is a lambda {@code next -> request -> …}.
 *
 * @see "m07 lesson, section Chain of Responsibility — middleware"
 */
public final class Middlewares {

    /** Header that carries the request id. */
    public static final String REQUEST_ID = "X-Request-Id";

    private Middlewares() {}

    /** Logs the request on the way in and the status on the way out. */
    public static Middleware logging(List<String> log) {
        Objects.requireNonNull(log, "log");
        return next -> request -> {
            log.add("-> " + request.method() + " " + request.path());
            Response response = next.handle(request);
            log.add("<- " + response.status() + " " + request.method() + " " + request.path());
            return response;
        };
    }

    /** Short-circuits with 401 unless the request carries {@code Authorization: Bearer <known token>}. */
    public static Middleware authentication(Set<String> tokens) {
        Set<String> known = Set.copyOf(tokens);
        return next -> request -> request.header("Authorization")
                .filter(value -> value.startsWith("Bearer ") && known.contains(value.substring("Bearer ".length())))
                .map(_ -> next.handle(request))
                .orElseGet(() -> new Response(401, "unauthorized"));
    }

    /** Turns an exception thrown further down the chain into a 500 response carrying its message. */
    public static Middleware errorBoundary() {
        return next -> request -> {
            try {
                return next.handle(request);
            } catch (RuntimeException e) {
                return new Response(500, "internal error: " + e.getMessage());
            }
        };
    }

    /** Adds an {@value #REQUEST_ID} header taken from {@code ids} before passing the request on. */
    public static Middleware requestId(Supplier<String> ids) {
        Objects.requireNonNull(ids, "ids");
        return next -> request -> next.handle(request.withHeader(REQUEST_ID, ids.get()));
    }
}
