package io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware;

import java.util.List;
import java.util.Objects;

/**
 * Builds the chain: the first middleware in the list is the outermost one, so requests pass the middlewares in
 * declared order and responses come back in reverse order.
 *
 * @see "m07 lesson, section Chain of Responsibility — middleware"
 */
public final class Pipeline {

    private Pipeline() {}

    public static Handler of(List<Middleware> middlewares, Handler endpoint) {
        Handler handler = Objects.requireNonNull(endpoint, "endpoint");
        for (Middleware middleware : middlewares.reversed()) { // wrap from the inside out
            handler = middleware.wrap(handler);
        }
        return handler;
    }
}
