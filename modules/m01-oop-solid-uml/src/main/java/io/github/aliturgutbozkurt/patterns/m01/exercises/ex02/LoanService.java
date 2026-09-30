package io.github.aliturgutbozkurt.patterns.m01.exercises.ex02;

/** GIVEN — do not modify. The library's lending rules. */
public interface LoanService {

    /**
     * Lends {@code book} to {@code member} for 14 days from today's date (according to the injected clock).
     *
     * @throws IllegalStateException if the member already has 3 active loans
     * @throws NullPointerException if an argument is {@code null}
     */
    Loan borrow(Member member, Book book);

    /** Ends the loan. */
    void giveBack(Loan loan);

    /**
     * Sends one message {@code "Overdue: <title> (due <yyyy-MM-dd>)"} per overdue loan (due date before today).
     *
     * @return how many reminders were sent
     */
    int remindOverdue();
}
