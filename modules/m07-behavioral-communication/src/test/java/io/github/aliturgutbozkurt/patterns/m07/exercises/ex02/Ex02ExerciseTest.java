package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

import java.util.List;
import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m07-behavioral-communication test -Pexercises}. */
@Tag("exercise")
class Ex02ExerciseTest extends Ex02Contract {

    @Override
    protected ApprovalChain standardChain() {
        return ApprovalChains.standard();
    }

    @Override
    protected ApprovalChain chainOf(List<Approver> approvers) {
        return ApprovalChains.of(approvers);
    }

    @Override
    protected Approver policyCheck() {
        return new PolicyCheck();
    }

    @Override
    protected Approver teamLead() {
        return new TeamLead();
    }

    @Override
    protected Approver manager() {
        return new Manager();
    }

    @Override
    protected Approver director() {
        return new Director();
    }
}
