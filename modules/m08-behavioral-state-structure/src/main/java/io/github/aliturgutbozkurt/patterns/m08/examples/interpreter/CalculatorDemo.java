package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter;

import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.EvalException;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Evaluator;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Lexer;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.ParseException;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Parser;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Printer;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Program;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Simplifier;
import java.util.List;
import java.util.Locale;

/** Run: {@code java modules/m08-behavioral-state-structure/src/main/java/io/github/aliturgutbozkurt/patterns/m08/examples/interpreter/CalculatorDemo.java} */
public final class CalculatorDemo {

    private CalculatorDemo() {}

    public static void main(String[] args) {
        System.out.print(String.format(Locale.ROOT, "%-38s %8s  %s\n", "source", "value", "printed"));
        for (String source : List.of("2 + 3 * 4", "8 - 3 - 2", "(8 - 3) - 2", "8 - (3 - 2)", "-2 * 3",
                "let x = 2 in let x = x + 1 in x * x")) {
            Expr tree = Parser.parse(source);
            System.out.print(String.format(Locale.ROOT, "%-38s %8d  %s\n", source, Evaluator.evaluate(tree),
                    Printer.print(tree)));
        }

        System.out.println("tokens:   " + Lexer.tokenize("-(a + 12)"));
        System.out.println("tree:     " + Parser.parse("-(a + 12)"));
        String messy = "(x * 1 + 0) * (2 + 3)";
        System.out.println("simplify: " + messy + "  =>  " + Printer.print(Simplifier.simplify(Parser.parse(messy))));

        for (String source : List.of("y + 1", "10 / (5 - 5)", "9223372036854775807 + 1", "(1 + 23")) {
            System.out.println("error:    " + source + "  =>  " + failure(source));
        }

        String program = """
                let price = 1200
                let qty = 3
                let discount = price * qty / 10
                price * qty - discount
                """;
        System.out.println("program:  " + program.lines().count() + " lines  =>  " + Program.run(program));
    }

    private static String failure(String source) {
        try {
            return "no error, value " + Evaluator.evaluate(Parser.parse(source));
        } catch (ParseException | EvalException e) {
            return e.getMessage();
        } catch (ArithmeticException e) {
            return "ArithmeticException: " + e.getMessage();
        }
    }
}
