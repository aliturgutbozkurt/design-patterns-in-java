package io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders;

/**
 * Thrown when an order is saved from a stale version: someone else saved it in between. The caller reloads,
 * re-applies its change and tries again.
 *
 * @see "m11 lesson, section Repository — optimistic versioning"
 */
public final class ConcurrentUpdateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final long expectedVersion;
    private final long actualVersion;

    public ConcurrentUpdateException(String orderId, long expectedVersion, long actualVersion) {
        super(orderId + " was changed concurrently: expected version " + expectedVersion + " but found " + actualVersion);
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

    /** The version the caller loaded. */
    public long expectedVersion() {
        return expectedVersion;
    }

    /** The version that is stored now. */
    public long actualVersion() {
        return actualVersion;
    }
}
