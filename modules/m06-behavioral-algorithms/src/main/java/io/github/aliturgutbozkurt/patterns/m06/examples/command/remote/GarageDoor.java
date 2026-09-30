package io.github.aliturgutbozkurt.patterns.m06.examples.command.remote;

/**
 * Receiver: a garage door that opens and closes.
 *
 * @see "m06 lesson, section Command"
 */
public final class GarageDoor {

    private boolean open;

    public void open() {
        open = true;
    }

    public void close() {
        open = false;
    }

    public boolean isOpen() {
        return open;
    }
}
