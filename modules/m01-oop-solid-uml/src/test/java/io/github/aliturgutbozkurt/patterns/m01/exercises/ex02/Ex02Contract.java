package io.github.aliturgutbozkurt.patterns.m01.exercises.ex02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.lang.reflect.Constructor;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Assignment 02 — library loans: DIP + LSP. Each test is one acceptance criterion of the brief. */
public abstract class Ex02Contract {

    protected abstract LoanStore store();

    protected abstract LoanService service(LoanStore store, Notifier notifier, Clock clock);

    /** A test double for time: starts at 2026-10-01 10:00 UTC and moves only when the test says so. */
    static final class AdjustableClock extends Clock {
        private Instant now = Instant.parse("2026-10-01T10:00:00Z");

        void advanceDays(long days) {
            now = now.plus(Duration.ofDays(days));
        }

        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { throw new UnsupportedOperationException(); }
        @Override public Instant instant() { return now; }
    }

    /** A test double for notifications: records every message. */
    static final class RecordingNotifier implements Notifier {
        final List<String> messages = new ArrayList<>();

        @Override
        public void send(Member member, String message) {
            messages.add(member.id() + ": " + message);
        }
    }

    private static final Member ADA = new Member("M-1", "Ada");
    private static final Member ALAN = new Member("M-2", "Alan");
    private static final Book PATTERNS = new Book("978-0-00-000001-1", "Design Patterns in Java");
    private static final Book REFACTORING = new Book("978-0-00-000002-8", "Refactoring Basics");
    private static final Book CLEAN = new Book("978-0-00-000003-5", "Readable Code");
    private static final Book TESTING = new Book("978-0-00-000004-2", "Testing in Practice");

    private final AdjustableClock clock = new AdjustableClock();
    private final RecordingNotifier notifier = new RecordingNotifier();

    private LoanService newService() {
        return service(store(), notifier, clock);
    }

    @Test
    void dueDateIsFourteenDaysAfterClockDate() {
        assertThat(newService().borrow(ADA, PATTERNS)).isEqualTo(new Loan(PATTERNS, ADA, LocalDate.of(2026, 10, 15)));
        clock.advanceDays(20);
        assertThat(newService().borrow(ADA, PATTERNS).dueDate()).isEqualTo(LocalDate.of(2026, 11, 4));
    }

    @Test
    void refusesFourthActiveLoan() {
        var service = newService();
        service.borrow(ADA, PATTERNS);
        service.borrow(ADA, REFACTORING);
        service.borrow(ADA, CLEAN);
        assertThatIllegalStateException().isThrownBy(() -> service.borrow(ADA, TESTING));
        assertThat(service.borrow(ALAN, TESTING).member()).isEqualTo(ALAN);
    }

    @Test
    void givingBackFreesASlot() {
        var service = newService();
        var first = service.borrow(ADA, PATTERNS);
        service.borrow(ADA, REFACTORING);
        service.borrow(ADA, CLEAN);
        service.giveBack(first);
        assertThat(service.borrow(ADA, TESTING).book()).isEqualTo(TESTING);
    }

    @Test
    void remindsOnlyOverdueLoans() {
        var service = newService();
        service.borrow(ADA, PATTERNS);           // due 2026-10-15
        clock.advanceDays(10);
        service.borrow(ALAN, REFACTORING);       // due 2026-10-25
        clock.advanceDays(10);                   // today 2026-10-21
        assertThat(service.remindOverdue()).isEqualTo(1);
        assertThat(notifier.messages).containsExactly("M-1: Overdue: Design Patterns in Java (due 2026-10-15)");
    }

    @Test
    void loanDueTodayIsNotOverdue() {
        var service = newService();
        service.borrow(ADA, PATTERNS);
        clock.advanceDays(14);                   // today is the due date
        assertThat(service.remindOverdue()).isZero();
        clock.advanceDays(1);
        assertThat(service.remindOverdue()).isEqualTo(1);
    }

    @Test
    void reminderMessageFormat() {
        var service = newService();
        service.borrow(ALAN, CLEAN);
        clock.advanceDays(30);
        service.remindOverdue();
        assertThat(notifier.messages).containsExactly("M-2: Overdue: Readable Code (due 2026-10-15)");
    }

    @Test
    void worksWithAnyNotifier() {
        Notifier silent = (member, message) -> { };
        var service = service(store(), silent, clock);
        service.borrow(ADA, PATTERNS);
        service.borrow(ALAN, CLEAN);
        clock.advanceDays(15);
        assertThat(service.remindOverdue()).isEqualTo(2);
    }

    @Test
    void storeContractHolds() {
        var store = store();
        var first = new Loan(PATTERNS, ADA, LocalDate.of(2026, 10, 15));
        var second = new Loan(CLEAN, ALAN, LocalDate.of(2026, 10, 16));
        var third = new Loan(REFACTORING, ADA, LocalDate.of(2026, 10, 17));
        store.save(first);
        store.save(second);
        store.save(third);
        assertThat(store.activeLoansOf(ADA)).containsExactly(first, third);
        assertThat(store.allActive()).containsExactly(first, second, third);
        store.remove(first);
        store.remove(first);                     // removing twice is harmless
        assertThat(store.activeLoansOf(ADA)).containsExactly(third);
        assertThat(store.activeLoansOf(new Member("M-9", "Nobody"))).isEmpty();
    }

    @Test
    void constructorDependsOnlyOnAbstractions() {
        Constructor<?>[] constructors = newService().getClass().getConstructors();
        assertThat(constructors).isNotEmpty();
        for (Constructor<?> constructor : constructors) {
            assertThat(Arrays.asList(constructor.getParameterTypes()))
                    .as("parameters of %s", constructor)
                    .allMatch(type -> type.isInterface() || type == Clock.class);
        }
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> service(null, notifier, clock));
        assertThatNullPointerException().isThrownBy(() -> service(store(), null, clock));
        assertThatNullPointerException().isThrownBy(() -> service(store(), notifier, null));
        assertThatNullPointerException().isThrownBy(() -> newService().borrow(null, PATTERNS));
        assertThatNullPointerException().isThrownBy(() -> newService().borrow(ADA, null));
    }
}
