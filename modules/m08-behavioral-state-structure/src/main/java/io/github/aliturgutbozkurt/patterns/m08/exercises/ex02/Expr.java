package io.github.aliturgutbozkurt.patterns.m08.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. The abstract syntax tree of the mini language (numbers, booleans, {@code if}). */
public sealed interface Expr {

    record Num(long value) implements Expr {}

    record Bool(boolean value) implements Expr {}

    record Var(String name) implements Expr {
        public Var {
            Objects.requireNonNull(name, "name");
        }
    }

    record Unary(UnaryOp op, Expr operand) implements Expr {
        public Unary {
            Objects.requireNonNull(op, "op");
            Objects.requireNonNull(operand, "operand");
        }
    }

    record Binary(BinaryOp op, Expr left, Expr right) implements Expr {
        public Binary {
            Objects.requireNonNull(op, "op");
            Objects.requireNonNull(left, "left");
            Objects.requireNonNull(right, "right");
        }
    }

    /** {@code if condition then then else otherwise}. */
    record If(Expr condition, Expr then, Expr otherwise) implements Expr {
        public If {
            Objects.requireNonNull(condition, "condition");
            Objects.requireNonNull(then, "then");
            Objects.requireNonNull(otherwise, "otherwise");
        }
    }
}
