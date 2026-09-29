package io.github.aliturgutbozkurt.patterns.m07.examples.mediator.chat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Mediator: participants only talk to the room, and the room owns every routing rule — broadcast to everyone else,
 * {@code @name} direct messages, and a notice back to the sender when the addressee is unknown.
 *
 * @see "m07 lesson, section Mediator"
 */
public final class ChatRoom {

    private final String name;
    private final Map<String, Participant> members = new LinkedHashMap<>(); // join order = delivery order

    public ChatRoom(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    /** Adds a participant; names are unique within the room. */
    public Participant join(String participantName) {
        Objects.requireNonNull(participantName, "participantName");
        if (members.containsKey(participantName)) {
            throw new IllegalArgumentException(participantName + " is already in " + name);
        }
        var participant = new Participant(participantName, this);
        members.put(participantName, participant);
        return participant;
    }

    /** Removes a participant; nothing is delivered to it afterwards. */
    public void leave(Participant participant) {
        members.remove(participant.name(), participant);
    }

    /** Routes {@code text} from {@code sender}: {@code "@bob hi"} goes to bob only, anything else to everyone else. */
    public void send(Participant sender, String text) {
        Objects.requireNonNull(text, "text");
        if (members.get(sender.name()) != sender) {
            throw new IllegalStateException(sender.name() + " is not in " + name);
        }
        if (text.startsWith("@")) {
            int space = text.indexOf(' ');
            String addressee = space < 0 ? text.substring(1) : text.substring(1, space);
            String body = space < 0 ? "" : text.substring(space + 1);
            Participant recipient = members.get(addressee);
            if (recipient == null) {
                sender.receive(name + ": nobody called '" + addressee + "' is here");
            } else {
                recipient.receive("(private) " + sender.name() + ": " + body);
            }
            return;
        }
        for (Participant member : members.values()) {
            if (member != sender) {
                member.receive(sender.name() + ": " + text);
            }
        }
    }
}
