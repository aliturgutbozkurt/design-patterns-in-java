package io.github.aliturgutbozkurt.patterns.m11.examples.refactoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate.DomainEventDispatcher;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.Analytics;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.CrmClient;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.Mailer;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.after.SignUpReactions;
import io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.after.UserRegistered;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotificationsRefactoringTest {

    /** One spy for all three side effects: records every call with its arguments, in order. */
    private final List<String> calls = new ArrayList<>();
    private final Mailer mailer = email -> calls.add("mail " + email);
    private final CrmClient crm = email -> calls.add("crm " + email);
    private final Analytics analytics = (event, email) -> calls.add("analytics " + event + " " + email);
    private final List<RuntimeException> errors = new ArrayList<>();
    private final DomainEventDispatcher<UserRegistered> events = new DomainEventDispatcher<>(errors::add);

    private io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.after.RegistrationService after() {
        SignUpReactions.subscribe(events, mailer, crm, analytics);
        return new io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.after
                .RegistrationService(events);
    }

    @Test
    void bothVersionsRecordTheSameSideEffects() {
        var before = new io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.before
                .RegistrationService(mailer, crm, analytics);
        before.register("ada@example.com");
        before.register("alan@example.com");
        List<String> expected = List.copyOf(calls);
        calls.clear();

        var after = after();
        after.register("ada@example.com");
        after.register("alan@example.com");

        assertThat(calls).isEqualTo(expected).containsExactly(
                "mail ada@example.com", "crm ada@example.com", "analytics signup ada@example.com",
                "mail alan@example.com", "crm alan@example.com", "analytics signup alan@example.com");
        assertThat(errors).isEmpty();
    }

    @Test
    void afterHasOneCollaboratorInsteadOfThree() {
        assertThat(io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.before
                .RegistrationService.class.getConstructors()[0].getParameterCount()).isEqualTo(3);
        assertThat(io.github.aliturgutbozkurt.patterns.m11.examples.refactoring.notifications.after
                .RegistrationService.class.getConstructors()[0].getParameterCount()).isEqualTo(1);
    }

    @Test
    void aFourthReactionNeedsNoChangeToTheService() {
        var after = after();
        events.subscribe(UserRegistered.class, registered -> calls.add("rewards " + registered.email()));
        after.register("ada@example.com");
        assertThat(calls).endsWith("rewards ada@example.com").hasSize(4);
    }

    @Test
    void invalidEmailIsRejectedBeforeAnySideEffect() {
        assertThatIllegalArgumentException().isThrownBy(() -> after().register("not-an-email"));
        assertThat(calls).isEmpty();
    }

    @Test
    void demoPrintsBothVersionsAndTheNewReaction() {
        assertThat(Console.capture(() -> NotificationsRefactoringDemo.main(new String[0]))).isEqualTo("""
                before (3 collaborators):
                  mail: welcome ada@example.com
                  crm: contact ada@example.com
                  analytics: signup ada@example.com
                after (1 collaborator):
                  mail: welcome ada@example.com
                  crm: contact ada@example.com
                  analytics: signup ada@example.com
                after, with a fourth reaction and no change to RegistrationService:
                  mail: welcome alan@example.com
                  crm: contact alan@example.com
                  analytics: signup alan@example.com
                  rewards: 100 points for alan@example.com
                """);
    }
}
