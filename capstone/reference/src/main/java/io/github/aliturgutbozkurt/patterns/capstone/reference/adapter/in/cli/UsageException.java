package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.in.cli;

/** The arguments do not fit the command; the CLI answers with the command's usage line. */
final class UsageException extends RuntimeException {

    // Exceptions are Serializable; -Xlint:all asks every serializable class for an explicit version id.
    private static final long serialVersionUID = 1L;

    UsageException() {
        super("usage", null, false, false);
    }
}
