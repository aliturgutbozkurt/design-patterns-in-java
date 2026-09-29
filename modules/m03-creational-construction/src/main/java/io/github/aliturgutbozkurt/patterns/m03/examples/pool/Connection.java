package io.github.aliturgutbozkurt.patterns.m03.examples.pool;

/**
 * An expensive resource — think of a database connection that takes a TCP and TLS handshake to open.
 *
 * @see "m03 lesson, section Object Pool"
 */
public interface Connection {

    int id();

    String query(String sql);
}
