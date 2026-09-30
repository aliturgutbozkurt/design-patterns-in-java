package io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware;

/**
 * A link of the chain that wraps its successor: it may act before {@code next}, after it, or not call it at all
 * (short-circuit) — like servlet {@code Filter} or {@code com.sun.net.httpserver.Filter}.
 *
 * @see "m07 lesson, section Chain of Responsibility — middleware"
 */
@FunctionalInterface
public interface Middleware {

    Handler wrap(Handler next);
}
