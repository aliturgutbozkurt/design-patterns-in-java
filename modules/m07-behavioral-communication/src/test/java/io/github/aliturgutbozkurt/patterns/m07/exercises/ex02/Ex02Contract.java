package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/** Assignment 02 — expense approval chain. Each test is one acceptance criterion of the brief. */
public abstract class Ex02Contract {

    protected abstract ApprovalChain standardChain();

    protected abstract ApprovalChain chainOf(List<Approver> approvers);

    protected abstract Approver policyCheck();

    protected abstract Approver teamLead();

    protected abstract Approver manager();

    protected abstract Approver director();

    private static Expense expense(Category category, long amountCents) {
        return new Expense("E-1", "ada", category, amountCents);
    }

    private ApprovalResult submit(Category category, long amountCents) {
        return standardChain().submit(expense(category, amountCents));
    }

    @Test
    void smallTravelExpenseApprovedByTeamLead() {
        assertThat(submit(Category.TRAVEL, 120_00)).isEqualTo(
                new ApprovalResult("E-1", new Approved("team lead"), List.of("policy check", "team lead")));
    }

    @Test
    void equipmentSkipsTeamLead() {
        assertThat(submit(Category.EQUIPMENT, 120_00)).isEqualTo(new ApprovalResult(
                "E-1", new Approved("manager"), List.of("policy check", "team lead", "manager")));
    }

    @Test
    void mediumExpenseEscalatesToManager() {
        assertThat(submit(Category.TRAINING, 2_000_00).decision()).isEqualTo(new Approved("manager"));
    }

    @Test
    void largeExpenseEscalatesToDirector() {
        assertThat(submit(Category.TRAVEL, 12_000_00)).isEqualTo(new ApprovalResult("E-1", new Approved("director"),
                List.of("policy check", "team lead", "manager", "director")));
    }

    @Test
    void tooLargeExpenseRejectedAtEndOfChain() {
        assertThat(submit(Category.EQUIPMENT, 25_000_00)).isEqualTo(new ApprovalResult("E-1",
                new Rejected("chain", "no approver could decide"),
                List.of("policy check", "team lead", "manager", "director")));
    }

    @Test
    void policyCheckRejectsExpensiveMealsBeforeAnyApprover() {
        assertThat(submit(Category.MEALS, 100_01)).isEqualTo(new ApprovalResult(
                "E-1", new Rejected("policy check", "meals above 100.00"), List.of("policy check")));
    }

    @Test
    void policyCheckRejectsNonPositiveAmounts() {
        assertThat(submit(Category.TRAVEL, 0).decision())
                .isEqualTo(new Rejected("policy check", "amount must be positive"));
        assertThat(submit(Category.TRAVEL, -5_00).trail()).containsExactly("policy check");
    }

    @Test
    void boundaryAmountsBelongToTheLowerApprover() {
        assertThat(submit(Category.MEALS, 100_00).decision()).isEqualTo(new Approved("team lead"));
        assertThat(submit(Category.TRAVEL, 500_00).decision()).isEqualTo(new Approved("team lead"));
        assertThat(submit(Category.TRAVEL, 500_01).decision()).isEqualTo(new Approved("manager"));
        assertThat(submit(Category.TRAVEL, 5_000_00).decision()).isEqualTo(new Approved("manager"));
        assertThat(submit(Category.TRAVEL, 5_000_01).decision()).isEqualTo(new Approved("director"));
        assertThat(submit(Category.TRAVEL, 20_000_00).decision()).isEqualTo(new Approved("director"));
        assertThat(submit(Category.TRAVEL, 20_000_01).decision())
                .isEqualTo(new Rejected("chain", "no approver could decide"));
    }

    @Test
    void trailListsReviewersInOrder() {
        assertThat(submit(Category.EQUIPMENT, 6_000_00).trail())
                .containsExactly("policy check", "team lead", "manager", "director");
    }

    @Test
    void chainStopsAtFirstDecision() {
        var reviews = new AtomicInteger();
        Approver counting = new Approver() {
            @Override
            public String name() {
                return "counter";
            }

            @Override
            public Optional<Decision> review(Expense expense) {
                reviews.incrementAndGet();
                return Optional.empty();
            }
        };
        var result = chainOf(List.of(teamLead(), counting)).submit(expense(Category.TRAVEL, 10_00));
        assertThat(result.decision()).isEqualTo(new Approved("team lead"));
        assertThat(result.trail()).containsExactly("team lead");
        assertThat(reviews).hasValue(0);
    }

    @Test
    void customChainUsesOnlyGivenApprovers() {
        var chain = chainOf(List.of(director(), manager()));
        assertThat(chain.submit(expense(Category.TRAVEL, 10_00)))
                .isEqualTo(new ApprovalResult("E-1", new Approved("director"), List.of("director")));
        assertThat(chainOf(List.of(policyCheck(), manager())).submit(expense(Category.EQUIPMENT, 30_000_00)))
                .isEqualTo(new ApprovalResult("E-1", new Rejected("chain", "no approver could decide"),
                        List.of("policy check", "manager")));
    }

    @Test
    void emptyChainRejects() {
        assertThat(chainOf(List.of()).submit(expense(Category.TRAVEL, 10_00))).isEqualTo(
                new ApprovalResult("E-1", new Rejected("chain", "no approver could decide"), List.of()));
    }

    @Test
    void trailIsImmutable() {
        List<String> trail = submit(Category.TRAVEL, 10_00).trail();
        assertThatThrownBy(() -> trail.add("intruder")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejectsNullExpense() {
        assertThatNullPointerException().isThrownBy(() -> standardChain().submit(null));
    }
}
