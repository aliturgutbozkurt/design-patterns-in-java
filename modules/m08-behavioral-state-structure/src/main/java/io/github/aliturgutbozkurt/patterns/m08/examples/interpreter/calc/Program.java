package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Let;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * A multi-line program: zero or more {@code let name = expression} lines, then one expression. Blank lines are
 * ignored. The lines become nested {@link Let}s, so a program is just a bigger expression.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public final class Program {

    private record Line(int number, String text) {}

    private Program() {}

    public static long run(String source) {
        return Evaluator.evaluate(parse(source));
    }

    public static Expr parse(String source) {
        List<String> all = source.lines().toList(); // a text block's trailing newline adds no empty last line
        List<Line> lines = IntStream.range(0, all.size())
                .mapToObj(i -> new Line(i + 1, all.get(i).strip()))
                .filter(line -> !line.text().isEmpty())
                .toList();
        if (lines.isEmpty()) {
            throw new ParseException("empty program", 1);
        }
        if (lines.size() > Parser.MAX_DEPTH) {
            throw new ParseException("more than " + Parser.MAX_DEPTH + " lines", 1);
        }
        Line last = lines.getLast();
        Expr program = onLine(last, () -> Parser.parse(last.text()));
        for (Line line : lines.subList(0, lines.size() - 1).reversed()) {
            Parser.Binding binding = onLine(line, () -> Parser.parseBinding(line.text()));
            program = new Let(binding.name(), binding.value(), program);
        }
        return program;
    }

    private static <T> T onLine(Line line, Supplier<T> parse) {
        try {
            return parse.get();
        } catch (ParseException e) {
            throw e.onLine(line.number());
        }
    }
}
