package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter;

import static io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Op.ADD;
import static io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Op.DIV;
import static io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Op.MUL;
import static io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Op.SUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.EvalException;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Evaluator;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Binary;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Let;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Neg;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Num;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Var;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Lexeme;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Lexer;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.ParseException;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Parser;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Printer;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Program;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Simplifier;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Token;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Token.Symbol;
import io.github.aliturgutbozkurt.patterns.m08.support.Console;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class CalculatorTest {

    private static final Var A = new Var("a");
    private static final Var B = new Var("b");
    private static final Var C = new Var("c");
    private static final Var X = new Var("x");
    private static final Map<String, Long> ENV = Map.of("a", 7L, "b", 3L, "c", 2L, "x", 5L);

    private static Num num(long value) {
        return new Num(value);
    }

    static Stream<Expr> trees() {
        Let letX = new Let("x", num(1), X);
        return Stream.of(
                num(7), X,
                new Binary(ADD, A, new Binary(MUL, B, C)),
                new Binary(MUL, new Binary(ADD, A, B), C),
                new Binary(SUB, A, new Binary(SUB, B, C)),
                new Binary(SUB, new Binary(SUB, A, B), C),
                new Binary(ADD, A, new Binary(ADD, B, C)),
                new Binary(DIV, A, new Binary(MUL, B, C)),
                new Neg(new Neg(X)),
                new Neg(new Binary(ADD, A, num(1))),
                new Binary(MUL, new Neg(A), new Neg(num(2))),
                new Let("x", num(2), new Binary(MUL, X, X)),
                new Let("x", new Let("y", num(1), new Var("y")), X),
                new Binary(ADD, letX, num(2)),
                new Binary(ADD, num(2), letX),
                new Neg(letX));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            8 - 3 - 2       | 3
            2 + 3 * 4       | 14
            -2 * 3          | -6
            20 / 2 / 5      | 2
            2 * (3 + 4)     | 14
            8 - (3 - 2)     | 7
            -7 / 2          | -3
            """)
    void respectsPrecedenceAndLeftAssociativity(String source, long expected) {
        assertThat(Evaluator.evaluate(Parser.parse(source))).isEqualTo(expected);
    }

    @Test
    void parsesIntoTheExpectedTreeShape() {
        assertThat(Parser.parse("8 - 3 - 2")).isEqualTo(new Binary(SUB, new Binary(SUB, num(8), num(3)), num(2)));
        assertThat(Parser.parse("-2 * 3")).isEqualTo(new Binary(MUL, new Neg(num(2)), num(3)));
        assertThat(Parser.parse("let x = 1 in x + 1")).isEqualTo(new Let("x", num(1), new Binary(ADD, X, num(1))));
    }

    @Test
    void lexerProducesTokensWithColumns() {
        assertThat(Lexer.tokenize("-(a + 12)")).containsExactly(
                new Lexeme(Symbol.MINUS, 1), new Lexeme(Symbol.LPAREN, 2), new Lexeme(new Token.Ident("a"), 3),
                new Lexeme(Symbol.PLUS, 5), new Lexeme(new Token.Number(12), 7), new Lexeme(Symbol.RPAREN, 9),
                new Lexeme(new Token.End(), 10));
        assertThat(Lexer.tokenize("let x in").stream().map(Lexeme::token))
                .containsExactly(new Token.Keyword("let"), new Token.Ident("x"), new Token.Keyword("in"),
                        new Token.End());
    }

    @ParameterizedTest
    @MethodSource("trees")
    void printedTextParsesBackToTheSameTree(Expr tree) {
        assertThat(Parser.parse(Printer.print(tree))).isEqualTo(tree);
    }

    @Test
    void printerKeepsOnlyNecessaryParentheses() {
        assertThat(Printer.print(new Binary(SUB, A, new Binary(SUB, B, C)))).isEqualTo("a - (b - c)");
        assertThat(Printer.print(new Binary(SUB, new Binary(SUB, A, B), C))).isEqualTo("a - b - c");
        assertThat(Printer.print(Parser.parse("(a - b) - c"))).isEqualTo("a - b - c");
        assertThat(Printer.print(new Binary(MUL, new Binary(ADD, A, B), C))).isEqualTo("(a + b) * c");
        assertThat(Printer.print(new Binary(ADD, A, new Binary(MUL, B, C)))).isEqualTo("a + b * c");
        assertThat(Printer.print(new Neg(new Binary(ADD, A, B)))).isEqualTo("-(a + b)");
        assertThat(Printer.print(new Binary(ADD, new Let("x", num(1), X), num(1)))).isEqualTo("(let x = 1 in x) + 1");
    }

    @Test
    void letBindsInItsBodyOnlyAndShadows() {
        assertThat(Evaluator.evaluate(Parser.parse("let x = 2 in let x = x + 1 in x * x"))).isEqualTo(9);
        assertThat(Evaluator.evaluate(Parser.parse("(let x = 1 in x) + x"), Map.of("x", 10L))).isEqualTo(11);
        assertThatThrownBy(() -> Evaluator.evaluate(Parser.parse("let y = (let x = 5 in x) in x")))
                .isInstanceOf(EvalException.class)
                .hasMessage("unknown variable: x");
    }

    @Test
    void unknownVariableIsAnEvalError() {
        assertThatThrownBy(() -> Evaluator.evaluate(Parser.parse("y + 1")))
                .isInstanceOf(EvalException.class)
                .hasMessage("unknown variable: y");
    }

    @Test
    void divisionByZeroIsAnEvalError() {
        assertThatThrownBy(() -> Evaluator.evaluate(Parser.parse("10 / (5 - 5)")))
                .isInstanceOf(EvalException.class)
                .hasMessage("division by zero");
    }

    @Test
    void overflowThrowsArithmeticException() {
        assertThatThrownBy(() -> Evaluator.evaluate(Parser.parse("9223372036854775807 + 1")))
                .isExactlyInstanceOf(ArithmeticException.class);
        assertThatThrownBy(() -> Evaluator.evaluate(Parser.parse("-9223372036854775807 - 2")))
                .isExactlyInstanceOf(ArithmeticException.class);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            (1 + 23              | expected ')' at column 8                       | 8
            1 + * 2              | expected a number, a name or '(' at column 5   | 5
            1 2                  | unexpected '2' at column 3                     | 3
            2 # 3                | unexpected character '#' at column 3           | 3
            let = 1 in 2         | expected a name at column 5                    | 5
            let x 1 in x         | expected '=' at column 7                       | 7
            let x = 1 x          | expected 'in' at column 11                     | 11
            99999999999999999999 | number too large at column 1                   | 1
            """)
    void parseErrorsReportTheColumn(String source, String message, int column) {
        var error = catchThrowableOfType(ParseException.class, () -> Parser.parse(source));
        assertThat(error).hasMessage(message);
        assertThat(error.column()).isEqualTo(column);
    }

    @Test
    void nestingDeeperThan200IsAParseError() {
        String ok = "(".repeat(200) + "1" + ")".repeat(200);
        assertThat(Evaluator.evaluate(Parser.parse(ok))).isEqualTo(1);
        String parens = "(".repeat(201) + "1" + ")".repeat(201);
        assertThatThrownBy(() -> Parser.parse(parens)).isInstanceOf(ParseException.class)
                .hasMessage("nesting deeper than 200 at column 201");
        assertThatThrownBy(() -> Parser.parse("-".repeat(1_000) + "1")).isInstanceOf(ParseException.class)
                .hasMessageStartingWith("nesting deeper than 200");
        assertThatThrownBy(() -> Parser.parse("1 + ".repeat(1_000) + "1")).isInstanceOf(ParseException.class)
                .hasMessageStartingWith("nesting deeper than 200");
    }

    @Test
    void evaluatesADirectlyBuiltTreeOfDepth1000() {
        Expr sum = num(0);
        for (int i = 1; i <= 1_000; i++) {
            sum = new Binary(ADD, sum, num(i));
        }
        assertThat(Evaluator.evaluate(sum)).isEqualTo(500_500);
    }

    @Test
    void simplifierFoldsConstantsAndAppliesIdentityRules() {
        assertThat(Simplifier.simplify(Parser.parse("(x * 1 + 0) * (2 + 3)"))).isEqualTo(new Binary(MUL, X, num(5)));
        assertThat(Simplifier.simplify(Parser.parse("y * 0"))).isEqualTo(num(0));
        assertThat(Simplifier.simplify(Parser.parse("0 * y"))).isEqualTo(num(0));
        assertThat(Simplifier.simplify(Parser.parse("0 + y"))).isEqualTo(new Var("y"));
        assertThat(Simplifier.simplify(Parser.parse("1 * y - 0"))).isEqualTo(new Var("y"));
        assertThat(Simplifier.simplify(Parser.parse("--x"))).isEqualTo(X);
        assertThat(Simplifier.simplify(Parser.parse("-(2 * 3)"))).isEqualTo(num(-6));
        assertThat(Simplifier.simplify(Parser.parse("let a = 2 * 3 in a + 0"))).isEqualTo(new Let("a", num(6),
                new Var("a")));
    }

    @Test
    void simplifierDoesNotFoldADivisionByZero() {
        assertThat(Simplifier.simplify(Parser.parse("10 / (5 - 5)"))).isEqualTo(new Binary(DIV, num(10), num(0)));
    }

    @ParameterizedTest
    @MethodSource("trees")
    void simplifyingKeepsTheValue(Expr tree) {
        assertThat(Evaluator.evaluate(Simplifier.simplify(tree), ENV)).isEqualTo(Evaluator.evaluate(tree, ENV));
    }

    @Test
    void aTextBlockProgramEvaluatesToTheExpectedValue() {
        String program = """
                let price = 1200
                let qty = 3

                let discount = price * qty / 10
                price * qty - discount
                """;
        assertThat(Program.run(program)).isEqualTo(3240);
    }

    @Test
    void programErrorsNameTheLine() {
        var error = catchThrowableOfType(ParseException.class, () -> Program.run("let x = 1\nlet = 2\nx"));
        assertThat(error).hasMessage("line 2: expected a name at column 5");
        assertThat(error.column()).isEqualTo(5);
        assertThatThrownBy(() -> Program.run("  \n")).isInstanceOf(ParseException.class)
                .hasMessage("empty program at column 1");
    }

    @Test
    void demoPrintsValuesPrintedFormsTokensSimplificationAndErrors() {
        assertThat(Console.capture(() -> CalculatorDemo.main(new String[0]))).isEqualTo("""
                source                                    value  printed
                2 + 3 * 4                                    14  2 + 3 * 4
                8 - 3 - 2                                     3  8 - 3 - 2
                (8 - 3) - 2                                   3  8 - 3 - 2
                8 - (3 - 2)                                   7  8 - (3 - 2)
                -2 * 3                                       -6  -2 * 3
                let x = 2 in let x = x + 1 in x * x           9  let x = 2 in let x = x + 1 in x * x
                tokens:   [-@1, (@2, a@3, +@5, 12@7, )@9, end@10]
                tree:     Neg[operand=Binary[op=ADD, left=Var[name=a], right=Num[value=12]]]
                simplify: (x * 1 + 0) * (2 + 3)  =>  x * 5
                error:    y + 1  =>  unknown variable: y
                error:    10 / (5 - 5)  =>  division by zero
                error:    9223372036854775807 + 1  =>  ArithmeticException: long overflow
                error:    (1 + 23  =>  expected ')' at column 8
                program:  4 lines  =>  3240
                """);
    }
}
