package io.github.aliturgutbozkurt.patterns.m07.examples.chain.middleware;

import java.util.Objects;

/**
 * An immutable HTTP-style response.
 *
 * @see "m07 lesson, section Chain of Responsibility — middleware"
 */
public record Response(int status, String body) {

    public Response {
        Objects.requireNonNull(body, "body");
    }
}
