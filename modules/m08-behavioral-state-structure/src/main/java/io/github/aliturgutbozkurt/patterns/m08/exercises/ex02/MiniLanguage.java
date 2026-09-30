package io.github.aliturgutbozkurt.patterns.m08.exercises.ex02;

import java.util.Map;
import java.util.Set;

/** Assignment 02 — your interpreter for the mini language: evaluator, printer, free variables. */
public class MiniLanguage implements Language {

    @Override
    public Value evaluate(Expr expr, Map<String, Value> env) {
        // TODO(ex02): one exhaustive switch over Expr (no default). Type-check every operand, short-circuit
        //  and/or, evaluate only the chosen if-branch, use Math.*Exact. See the brief for the exact messages.
        throw new UnsupportedOperationException("TODO(ex02): implement evaluate(Expr, Map)");
    }

    @Override
    public String print(Expr expr) {
        // TODO(ex02): parentheses only where precedence or associativity needs them (compare with the Printer in
        //  examples/interpreter/calc).
        throw new UnsupportedOperationException("TODO(ex02): implement print(Expr)");
    }

    @Override
    public Set<String> freeVariables(Expr expr) {
        throw new UnsupportedOperationException("TODO(ex02): implement freeVariables(Expr)");
    }
}
