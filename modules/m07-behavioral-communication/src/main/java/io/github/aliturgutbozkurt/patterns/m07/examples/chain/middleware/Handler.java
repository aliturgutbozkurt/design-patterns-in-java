package io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware;

/**
 * Anything that turns a request into a response: the endpoint, or the endpoint wrapped in middleware.
 *
 * @see "m07 lesson, section Chain of Responsibility — middleware"
 */
@FunctionalInterface
public interface Handler {

    Response handle(Request request);
}
