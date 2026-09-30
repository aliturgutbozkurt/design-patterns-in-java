package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request;

import java.util.Objects;
import java.util.Set;

/**
 * The authenticated caller of a request (immutable, like everything a scoped value carries should be).
 *
 * @see "m10 lesson, section Scoped Values"
 */
public record Principal(String name, Set<String> roles) {

    /** The identity used for internal work done on behalf of the platform. */
    public static final Principal SYSTEM = new Principal("system", Set.of("admin"));

    /** The fallback when no request is running. */
    public static final Principal ANONYMOUS = new Principal("anonymous", Set.of());

    public Principal {
        Objects.requireNonNull(name, "name");
        roles = Set.copyOf(roles);
    }
}
