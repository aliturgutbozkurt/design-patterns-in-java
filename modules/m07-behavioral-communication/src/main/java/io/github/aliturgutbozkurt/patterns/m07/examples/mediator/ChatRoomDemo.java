package io.github.aliturgutbozkurt.patterns.m07.examples.mediator;

import io.github.aliturgutbozkurt.patterns.m07.examples.mediator.chat.ChatRoom;
import io.github.aliturgutbozkurt.patterns.m07.examples.mediator.chat.Participant;
import java.util.List;

/** Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/mediator/ChatRoomDemo.java} */
public final class ChatRoomDemo {

    private ChatRoomDemo() {}

    public static void main(String[] args) {
        var room = new ChatRoom("#patterns");
        Participant ada = room.join("ada");
        Participant bob = room.join("bob");
        Participant cem = room.join("cem");

        ada.send("hello everyone");
        bob.send("@cem lunch at noon?");
        cem.send("@dave are you there?");
        cem.leave();
        ada.send("cem left early");

        for (Participant participant : List.of(ada, bob, cem)) {
            System.out.println(participant.name() + " " + participant.inbox());
        }
    }
}
