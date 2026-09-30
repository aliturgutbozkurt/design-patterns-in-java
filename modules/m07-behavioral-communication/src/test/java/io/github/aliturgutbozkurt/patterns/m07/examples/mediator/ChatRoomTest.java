package io.github.aliturgutbozkurt.patterns.m07.examples.mediator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m07.examples.mediator.chat.ChatRoom;
import io.github.aliturgutbozkurt.patterns.m07.examples.mediator.chat.Participant;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ChatRoomTest {

    private final ChatRoom room = new ChatRoom("#test");
    private final Participant ada = room.join("ada");
    private final Participant bob = room.join("bob");
    private final Participant cem = room.join("cem");

    @Test
    void broadcastReachesEveryoneExceptTheSender() {
        ada.send("hi");
        assertThat(ada.inbox()).isEmpty();
        assertThat(bob.inbox()).containsExactly("ada: hi");
        assertThat(cem.inbox()).containsExactly("ada: hi");
    }

    @Test
    void directMessageReachesOnlyTheAddressee() {
        ada.send("@cem psst");
        assertThat(cem.inbox()).containsExactly("(private) ada: psst");
        assertThat(bob.inbox()).isEmpty();
        assertThat(ada.inbox()).isEmpty();
    }

    @Test
    void unknownAddresseeSendsANoticeToTheSenderOnly() {
        ada.send("@zed hello?");
        assertThat(ada.inbox()).containsExactly("#test: nobody called 'zed' is here");
        assertThat(bob.inbox()).isEmpty();
        assertThat(cem.inbox()).isEmpty();
    }

    @Test
    void nothingIsDeliveredAfterLeaving() {
        bob.leave();
        ada.send("hi");
        ada.send("@bob still there?");
        assertThat(bob.inbox()).isEmpty();
        assertThat(ada.inbox()).containsExactly("#test: nobody called 'bob' is here");
        assertThatIllegalStateException().isThrownBy(() -> bob.send("hello")).withMessage("bob is not in #test");
    }

    @Test
    void namesAreUnique() {
        assertThatIllegalArgumentException().isThrownBy(() -> room.join("ada"));
    }

    @Test
    void participantHoldsNoReferenceToAnotherParticipant() {
        assertThat(Arrays.stream(Participant.class.getDeclaredFields()).<Class<?>>map(Field::getType).toList())
                .doesNotContain(Participant.class)
                .contains(ChatRoom.class);
    }

    @Test
    void demoPrintsEveryInbox() {
        assertThat(Console.capture(() -> ChatRoomDemo.main(new String[0]))).isEqualTo("""
                ada []
                bob [ada: hello everyone, ada: cem left early]
                cem [ada: hello everyone, (private) bob: lunch at noon?, #patterns: nobody called 'dave' is here]
                """);
    }
}
