package io.github.aliturgutbozkurt.patterns.m08.exercises.ex02;

import java.util.Map;
import java.util.Set;

/** GIVEN — do not modify. The three operations you implement over {@link Expr}. */
public interface Language {

    /** The value of {@code expr}, looking variables up in {@code env}. */
    Value evaluate(Expr expr, Map<String, Value> env);

    /** Source text with only the necessary parentheses: {@code Parser.parse(print(e))} equals {@code e}. */
    String print(Expr expr);

    /** Every variable name that occurs in {@code expr}. */
    Set<String> freeVariables(Expr expr);
}
