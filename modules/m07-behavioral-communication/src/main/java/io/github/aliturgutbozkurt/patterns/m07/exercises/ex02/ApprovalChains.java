package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

import java.util.List;

/** Assignment 02 — builds approval chains. */
public final class ApprovalChains {

    private ApprovalChains() {}

    /** Policy check → team lead → manager → director. */
    public static ApprovalChain standard() {
        // TODO(ex02): reuse of(...).
        throw new UnsupportedOperationException("TODO(ex02): implement ApprovalChains.standard()");
    }

    /** Any approvers, asked in the given order; the first decision wins. */
    public static ApprovalChain of(List<Approver> approvers) {
        // TODO(ex02): return an ApprovalChain that records every reviewer in the trail and stops at the first
        //             decision; if nobody decides: Rejected("chain", "no approver could decide").
        throw new UnsupportedOperationException("TODO(ex02): implement ApprovalChains.of(List)");
    }
}
