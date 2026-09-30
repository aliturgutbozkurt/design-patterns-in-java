package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.protection;

import java.util.Objects;

/**
 * The user on whose behalf the store is accessed.
 *
 * @see "m04 lesson, section Proxy"
 */
public record User(String name, Role role) {

    public User {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(role, "role");
    }
}
