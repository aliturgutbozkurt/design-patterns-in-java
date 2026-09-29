package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.protection;

import java.util.Objects;

/**
 * Protection proxy: checks the user's role before every call; a forbidden call never reaches the real store.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class ProtectedDocumentStore implements DocumentStore {

    private final DocumentStore target;
    private final User user;

    public ProtectedDocumentStore(DocumentStore target, User user) {
        this.target = Objects.requireNonNull(target, "target");
        this.user = Objects.requireNonNull(user, "user");
    }

    /** The permission rule: exhaustive over roles, so a new role will not compile until it is decided here. */
    static boolean allowed(Role role, Operation operation) {
        return switch (role) {
            case VIEWER -> operation == Operation.READ;
            case EDITOR -> operation != Operation.DELETE;
            case ADMIN -> true;
        };
    }

    @Override
    public String read(String id) {
        check(Operation.READ, id);
        return target.read(id);
    }

    @Override
    public void write(String id, String content) {
        check(Operation.WRITE, id);
        target.write(id, content);
    }

    @Override
    public void delete(String id) {
        check(Operation.DELETE, id);
        target.delete(id);
    }

    private void check(Operation operation, String id) {
        if (!allowed(user.role(), operation)) {
            throw new PermissionDeniedException(user, operation, id);
        }
    }
}
