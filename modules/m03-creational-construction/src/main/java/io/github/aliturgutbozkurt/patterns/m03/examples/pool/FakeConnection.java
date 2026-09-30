package io.github.aliturgutbozkurt.patterns.m03.examples.pool;

/**
 * A stand-in connection that echoes queries with its id, so tests can see which connection was reused.
 *
 * @see "m03 lesson, section Object Pool"
 */
public record FakeConnection(int id) implements Connection {

    @Override
    public String query(String sql) {
        return "conn-" + id + ": " + sql;
    }
}
