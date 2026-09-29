package io.github.aliturgutbozkurt.patterns.m07.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.ApprovalChain;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Approver;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Ex02Contract;
import java.util.List;

class Ex02SolutionTest extends Ex02Contract {

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
