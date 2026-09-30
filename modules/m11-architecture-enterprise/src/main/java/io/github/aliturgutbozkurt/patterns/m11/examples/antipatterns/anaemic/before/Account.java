package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.anaemic.before;

/**
 * ANTI-PATTERN — anaemic domain model: data with getters and setters, no behaviour. Every rule lives somewhere else,
 * and every caller can bypass it with a setter.
 *
 * @see "m11 lesson, section Anti-patterns — anaemic domain model"
 */
public final class Account {

    private String owner;
    private long balanceCents;
    private boolean closed;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getBalanceCents() {
        return balanceCents;
    }

    public void setBalanceCents(long balanceCents) {
        this.balanceCents = balanceCents;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
