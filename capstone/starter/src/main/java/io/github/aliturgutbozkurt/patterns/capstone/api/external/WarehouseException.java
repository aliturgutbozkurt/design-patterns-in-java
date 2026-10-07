package io.github.aliturgutbozkurt.patterns.capstone.api.external;

/**
 * GIVEN — do not modify. A warehouse operation failed; the message says why.
 *
 * @see "capstone brief §2.2 — Fulfilment (F9)"
 */
public class WarehouseException extends RuntimeException {

    // Exceptions are Serializable; -Xlint:all asks every serializable class for an explicit version id.
    private static final long serialVersionUID = 1L;

    public WarehouseException(String message) {
        super(message);
    }

    public WarehouseException(String message, Throwable cause) {
        super(message, cause);
    }
}
