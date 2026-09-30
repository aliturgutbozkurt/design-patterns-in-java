package io.github.aliturgutbozkurt.patterns.m07.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.ApprovalChain;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.ApprovalResult;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Approver;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Decision;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Rejected;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Reference solution for assignment 02: the chain is a list of approvers asked in order until one decides.
 *
 * @see "m07 lesson, section Chain of Responsibility"
 */
public final class ApprovalChains {

    private ApprovalChains() {}

    public static ApprovalChain standard() {
        return of(List.of(new PolicyCheck(), new TeamLead(), new Manager(), new Director()));
    }

    public static ApprovalChain of(List<Approver> approvers) {
        List<Approver> chain = List.copyOf(approvers);
        return expense -> {
            Objects.requireNonNull(expense, "expense");
            List<String> trail = new ArrayList<>();
            for (Approver approver : chain) {
                trail.add(approver.name());
                Optional<Decision> decision = approver.review(expense);
                if (decision.isPresent()) {
                    return new ApprovalResult(expense.id(), decision.get(), trail); // first decision wins
                }
            }
            return new ApprovalResult(expense.id(), new Rejected("chain", "no approver could decide"), trail);
        };
    }
}
