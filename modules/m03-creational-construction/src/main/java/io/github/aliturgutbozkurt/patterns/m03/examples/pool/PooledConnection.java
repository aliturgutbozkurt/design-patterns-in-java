package io.github.aliturgutbozkurt.patterns.m03.examples.pool;

/**
 * A lease on a pooled connection. {@link #close()} returns the connection to the pool exactly once; afterwards the
 * lease cannot be used.
 *
 * @see "m03 lesson, section Object Pool"
 */
public final class PooledConnection implements AutoCloseable {

    private final ConnectionPool pool;
    private Connection connection;

    PooledConnection(ConnectionPool pool, Connection connection) {
        this.pool = pool;
        this.connection = connection;
    }

    public String query(String sql) {
        if (connection == null) {
            throw new IllegalStateException("lease already closed");
        }
        return connection.query(sql);
    }

    /** Returns the connection to the pool; closing twice does nothing. */
    @Override
    public void close() {
        if (connection != null) {
            Connection returned = connection;
            connection = null;
            pool.release(returned);
        }
    }
}
