package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Colleague: knows only its {@link ChatRoom} — never another participant — and keeps an inbox of what it received.
 *
 * @see "m07 lesson, section Mediator"
 */
public final class Participant {

    private final String name;
    private final ChatRoom room;
    private final List<String> inbox = new ArrayList<>();

    Participant(String name, ChatRoom room) {
        this.name = Objects.requireNonNull(name, "name");
        this.room = Objects.requireNonNull(room, "room");
    }

    public String name() {
        return name;
    }

    /** Sends through the room; the room decides who receives it. */
    public void send(String text) {
        room.send(this, text);
    }

    public void leave() {
        room.leave(this);
    }

    void receive(String message) {
        inbox.add(message);
    }

    public List<String> inbox() {
        return List.copyOf(inbox);
    }
}
