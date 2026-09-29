package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.protection;

/**
 * Thrown by a protection proxy when the user may not perform an operation.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class PermissionDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PermissionDeniedException(User user, Operation operation, String id) {
        super(user.name() + " (" + user.role() + ") may not " + operation + " " + id);
    }
}
