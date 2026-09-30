package io.github.aliturgutbozkurt.patterns.m08.exercises.ex02;

import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.BinaryOp.ADD;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.BinaryOp.AND;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.BinaryOp.DIV;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.BinaryOp.EQ;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.BinaryOp.LE;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.BinaryOp.LT;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.BinaryOp.MUL;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.BinaryOp.OR;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.BinaryOp.SUB;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.UnaryOp.NEG;
import static io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.UnaryOp.NOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Binary;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Bool;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.If;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Num;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Unary;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Var;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Value.BoolValue;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Value.NumValue;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** Assignment 02 — mini expression language. Each test is one acceptance criterion of the brief. */
public abstract class Ex02Contract {

    private static final Map<String, Value> ENV = Map.of(
            "x", new NumValue(5), "flag", new BoolValue(true), "big", new NumValue(Long.MAX_VALUE));
    private static final Var A = new Var("a");
    private static final Var B = new Var("b");
    private static final Var C = new Var("c");
    private static final Var X = new Var("x");
    private static final Var FLAG = new Var("flag");

    /** Your {@link Language} implementation. */
    protected abstract Language newLanguage();

    private Language language;

    @BeforeEach
    void createLanguage() {
        language = newLanguage();
    }

    private Value eval(String source) {
        return language.evaluate(Parser.parse(source), ENV);
    }

    private long number(String source) {
        return ((NumValue) eval(source)).value();
    }

    private boolean bool(String source) {
        return ((BoolValue) eval(source)).value();
    }

    private void assertEvalError(String source, String message) {
        assertThatThrownBy(() -> eval(source)).isInstanceOf(EvalException.class).hasMessage(message);
    }

    private String printed(String source) {
        return language.print(Parser.parse(source));
    }

    private static Num num(long value) {
        return new Num(value);
    }

    static Stream<Expr> trees() {
        return Stream.of(
                num(42), new Bool(false), X,
                new Binary(ADD, A, new Binary(MUL, B, C)),
                new Binary(MUL, new Binary(ADD, A, B), C),
                new Binary(SUB, A, new Binary(SUB, B, C)),
                new Binary(SUB, new Binary(SUB, A, B), C),
                new Binary(DIV, A, new Binary(DIV, B, C)),
                new Binary(OR, new Binary(AND, A, B), C),
                new Binary(AND, A, new Binary(OR, B, C)),
                new Binary(EQ, new Binary(LT, A, B), FLAG),
                new Binary(LE, new Binary(ADD, A, num(1)), new Binary(MUL, B, num(2))),
                new Unary(NEG, new Unary(NEG, X)),
                new Unary(NOT, new Unary(NOT, FLAG)),
                new Unary(NOT, new Binary(EQ, A, B)),
                new Binary(EQ, new Unary(NOT, A), B),
                new Unary(NEG, new Binary(ADD, A, num(1))),
                new Unary(NOT, new Binary(AND, A, B)),
                new Binary(AND, new Unary(NOT, A), B),
                new If(new Binary(LT, X, num(3)), new Unary(NEG, X), new If(FLAG, num(0), num(1))),
                new If(new If(A, B, C), num(1), num(2)),
                new Binary(ADD, new If(FLAG, num(1), num(2)), num(3)),
                new Unary(NEG, new If(FLAG, num(1), num(2))));
    }

    @Test
    void evaluatesIntegerArithmetic() {
        assertThat(number("1 + 2 * 3")).isEqualTo(7);
        assertThat(number("10 - 4")).isEqualTo(6);
        assertThat(number("-x")).isEqualTo(-5);
        assertThat(number("x * x - 1")).isEqualTo(24);
    }

    @Test
    void respectsPrecedenceAndLeftAssociativity() {
        assertThat(number("8 - 3 - 2")).isEqualTo(3);
        assertThat(number("100 / 10 / 5")).isEqualTo(2);
        assertThat(number("(2 + 3) * 4")).isEqualTo(20);
        assertThat(bool("true or false and false")).isTrue();
        assertThat(bool("not true or true")).isTrue();
        assertThat(bool("1 + 1 = 2 and 2 * 2 = 4")).isTrue();
    }

    @Test
    void integerDivisionTruncatesTowardZero() {
        assertThat(number("7 / 2")).isEqualTo(3);
        assertThat(number("-7 / 2")).isEqualTo(-3);
        assertThat(number("7 / -2")).isEqualTo(-3);
    }

    @Test
    void divisionByZeroIsAnEvalError() {
        assertEvalError("1 / (x - 5)", "division by zero");
    }

    @Test
    void overflowThrowsArithmeticException() {
        assertThatThrownBy(() -> eval("big + 1")).isExactlyInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> eval("big * 2")).isExactlyInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> eval("0 - big - 2")).isExactlyInstanceOf(ArithmeticException.class);
    }

    @Test
    void comparesNumbersAndBooleans() {
        assertThat(bool("1 < 2")).isTrue();
        assertThat(bool("3 < 3")).isFalse();
        assertThat(bool("2 <= 2")).isTrue();
        assertThat(bool("x = 5")).isTrue();
        assertThat(bool("true = true")).isTrue();
        assertThat(bool("true = false")).isFalse();
        assertEvalError("true < 1", "type error: LT expects NUM but got BOOL");
    }

    @Test
    void mixedTypeEqualityIsATypeError() {
        assertEvalError("1 = true", "type error: EQ expects NUM but got BOOL");
        assertEvalError("flag = x", "type error: EQ expects BOOL but got NUM");
    }

    @Test
    void arithmeticOnBooleansIsATypeError() {
        assertEvalError("true + 1", "type error: ADD expects NUM but got BOOL");
        assertEvalError("1 * false", "type error: MUL expects NUM but got BOOL");
        assertEvalError("-flag", "type error: NEG expects NUM but got BOOL");
        assertEvalError("not 1", "type error: NOT expects BOOL but got NUM");
        assertEvalError("1 and true", "type error: AND expects BOOL but got NUM");
        assertEvalError("false or 1", "type error: OR expects BOOL but got NUM");
    }

    @Test
    void ifRequiresABooleanCondition() {
        assertEvalError("if 1 then 2 else 3", "type error: IF expects BOOL but got NUM");
        assertThat(number("if x < 10 then 1 else 2")).isEqualTo(1);
    }

    @Test
    void andOrShortCircuit() {
        assertThat(bool("false and 1 / 0 = 1")).isFalse();
        assertThat(bool("true or unknown")).isTrue();
        assertEvalError("true and 1 / 0 = 1", "division by zero");
        assertEvalError("false or unknown", "unknown variable: unknown");
    }

    @Test
    void ifEvaluatesOnlyTheChosenBranch() {
        assertThat(number("if true then 1 else 1 / 0")).isEqualTo(1);
        assertThat(number("if false then nope else 2")).isEqualTo(2);
        assertThat(eval("if flag then 1 else false")).isEqualTo(new NumValue(1));
    }

    @Test
    void looksUpVariablesInTheEnvironment() {
        assertThat(number("x + 1")).isEqualTo(6);
        assertThat(eval("flag")).isEqualTo(new BoolValue(true));
        assertThat(bool("not flag")).isFalse();
    }

    @Test
    void unknownVariableIsAnEvalError() {
        assertEvalError("y + 1", "unknown variable: y");
    }

    @Test
    void printsWithMinimalParentheses() {
        assertThat(printed("(a - b) - c")).isEqualTo("a - b - c");
        assertThat(printed("a - (b - c)")).isEqualTo("a - (b - c)");
        assertThat(printed("(a + b) * c")).isEqualTo("(a + b) * c");
        assertThat(printed("a + (b * c)")).isEqualTo("a + b * c");
        assertThat(printed("(a and b) or c")).isEqualTo("a and b or c");
        assertThat(printed("a and (b or c)")).isEqualTo("a and (b or c)");
        assertThat(printed("(a < b) = flag")).isEqualTo("(a < b) = flag");
        assertThat(printed("((x))")).isEqualTo("x");
        assertThat(printed("1+2")).isEqualTo("1 + 2");
    }

    @Test
    void printsNestedIfAndUnaryOperators() {
        assertThat(language.print(new Unary(NEG, new Unary(NEG, X)))).isEqualTo("--x");
        assertThat(language.print(new Unary(NOT, new Unary(NOT, FLAG)))).isEqualTo("not not flag");
        assertThat(language.print(new If(new Binary(LT, X, num(3)), new Unary(NEG, X), new If(FLAG, num(0),
                num(1))))).isEqualTo("if x < 3 then -x else if flag then 0 else 1");
        assertThat(language.print(new Binary(ADD, new If(FLAG, num(1), num(2)), num(3))))
                .isEqualTo("(if flag then 1 else 2) + 3");
        assertThat(language.print(new Unary(NOT, new Binary(AND, A, B)))).isEqualTo("not (a and b)");
        assertThat(language.print(new Unary(NOT, new Binary(EQ, A, B)))).isEqualTo("not a = b");
        assertThat(language.print(new Binary(EQ, new Unary(NOT, A), B))).isEqualTo("(not a) = b");
        assertThat(language.print(new Unary(NEG, new Binary(ADD, A, num(1))))).isEqualTo("-(a + 1)");
    }

    @ParameterizedTest
    @MethodSource("trees")
    void printedTextParsesBackToTheSameTree(Expr tree) {
        assertThat(Parser.parse(language.print(tree))).isEqualTo(tree);
    }

    @Test
    void collectsFreeVariables() {
        assertThat(language.freeVariables(Parser.parse("if a < b then c + a else not d")))
                .containsExactlyInAnyOrder("a", "b", "c", "d");
        assertThat(language.freeVariables(Parser.parse("1 + 2 * 3"))).isEmpty();
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> language.evaluate(null, ENV));
        assertThatNullPointerException().isThrownBy(() -> language.evaluate(num(1), null));
        assertThatNullPointerException().isThrownBy(() -> language.print(null));
        assertThatNullPointerException().isThrownBy(() -> language.freeVariables(null));
    }
}
