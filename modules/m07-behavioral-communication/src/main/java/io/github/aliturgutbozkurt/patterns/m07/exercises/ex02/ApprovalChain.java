package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

/** GIVEN — do not modify. Sends an expense along the chain until an approver decides. */
@FunctionalInterface
public interface ApprovalChain {

    ApprovalResult submit(Expense expense);
}
