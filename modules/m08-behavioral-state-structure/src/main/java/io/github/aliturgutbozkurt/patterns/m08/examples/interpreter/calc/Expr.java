package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

import java.util.Objects;

/**
 * The abstract syntax tree of the calculator language, as sealed records. It extends m05's expression Composite
 * ({@code Num}/{@code Add}/{@code Mul}/{@code Neg}) with variables, {@code let … in …}, and one {@code Binary} node
 * for all four operators. Operations live outside the tree: {@link Evaluator}, {@link Printer}, {@link Simplifier}.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public sealed interface Expr {

    record Num(long value) implements Expr {}

    record Var(String name) implements Expr {
        public Var {
            Objects.requireNonNull(name, "name");
        }
    }

    record Neg(Expr operand) implements Expr {
        public Neg {
            Objects.requireNonNull(operand, "operand");
        }
    }

    record Binary(Op op, Expr left, Expr right) implements Expr {
        public Binary {
            Objects.requireNonNull(op, "op");
            Objects.requireNonNull(left, "left");
            Objects.requireNonNull(right, "right");
        }
    }

    /** {@code let name = value in body}: {@code name} is visible in {@code body} only. */
    record Let(String name, Expr value, Expr body) implements Expr {
        public Let {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(value, "value");
            Objects.requireNonNull(body, "body");
        }
    }
}
