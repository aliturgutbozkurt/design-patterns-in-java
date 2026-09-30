package io.github.aliturgutbozkurt.patterns.m09.examples.result.core;

import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Err;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Ok;
import java.util.List;

/** Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/result/core/ResultBasicsDemo.java} */
public final class ResultBasicsDemo {

    private static final long UNIT_PRICE_CENTS = 250;

    private ResultBasicsDemo() {}

    static Result<Integer, String> parse(String text) {
        return Result.attempt(() -> Integer.parseInt(text.strip()), _ -> "not a number: \"" + text + "\"");
    }

    static Result<Integer, String> inRange(int quantity) {
        return quantity >= 1 && quantity <= 99
                ? Result.ok(quantity)
                : Result.err("out of range 1..99: " + quantity);
    }

    /** Two steps on the railway: the range check runs only if parsing worked. */
    static Result<Integer, String> quantity(String text) {
        return parse(text).flatMap(ResultBasicsDemo::inRange);
    }

    public static void main(String[] args) {
        List<String> inputs = List.of("3", " 12 ", "abc", "150");

        System.out.println("-- parse, then check the range (flatMap)");
        for (String input : inputs) {
            System.out.println(label(input) + " -> " + quantity(input));
        }

        System.out.println("-- price the good ones (map), then leave the railway (fold)");
        for (String input : inputs) {
            String line = quantity(input)
                    .map(q -> q + " x 2.50 = " + q * UNIT_PRICE_CENTS + " cents")
                    .fold(text -> text, error -> "rejected: " + error);
            System.out.println(label(input) + " -> " + line);
        }

        System.out.println("-- switch over Ok(var v) / Err(var e)");
        String reply = switch (quantity("abc")) {
            case Ok(var q) -> "reserve " + q;
            case Err(var e) -> "ask the customer again (" + e + ")";
        };
        System.out.println(label("abc") + " -> " + reply);

        System.out.println("-- many results at once");
        List<Result<Integer, String>> all = inputs.stream().map(ResultBasicsDemo::quantity).toList();
        System.out.println("sequence(3, 12)      = " + Results.sequence(all.subList(0, 2)));
        System.out.println("sequence(all four)   = " + Results.sequence(all));
        System.out.println("partition(all four)  = " + Results.partition(all));
    }

    private static String label(String input) {
        String quoted = "\"" + input + "\"";
        return quoted + " ".repeat(Math.max(0, 6 - quoted.length()));
    }
}
