package io.github.aliturgutbozkurt.patterns.m01.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.Book;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.Loan;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.LoanService;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.LoanStore;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.Member;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.Notifier;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Reference solution for assignment 02: every collaborator — storage, messaging and even time — comes in through
 * the constructor as an abstraction, so the rules can be tested with fakes.
 */
public class LibraryLoanService implements LoanService {

    private static final int MAX_ACTIVE_LOANS = 3;
    private static final int LOAN_DAYS = 14;

    private final LoanStore store;
    private final Notifier notifier;
    private final Clock clock;

    public LibraryLoanService(LoanStore store, Notifier notifier, Clock clock) {
        this.store = Objects.requireNonNull(store, "store");
        this.notifier = Objects.requireNonNull(notifier, "notifier");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public Loan borrow(Member member, Book book) {
        Objects.requireNonNull(member, "member");
        Objects.requireNonNull(book, "book");
        if (store.activeLoansOf(member).size() >= MAX_ACTIVE_LOANS) {
            throw new IllegalStateException(member.name() + " already has " + MAX_ACTIVE_LOANS + " active loans");
        }
        var loan = new Loan(book, member, today().plusDays(LOAN_DAYS));
        store.save(loan);
        return loan;
    }

    @Override
    public void giveBack(Loan loan) {
        store.remove(Objects.requireNonNull(loan, "loan"));
    }

    @Override
    public int remindOverdue() {
        LocalDate today = today();
        var overdue = store.allActive().stream().filter(loan -> loan.dueDate().isBefore(today)).toList();
        overdue.forEach(loan -> notifier.send(loan.member(),
                "Overdue: " + loan.book().title() + " (due " + loan.dueDate() + ")"));
        return overdue.size();
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
