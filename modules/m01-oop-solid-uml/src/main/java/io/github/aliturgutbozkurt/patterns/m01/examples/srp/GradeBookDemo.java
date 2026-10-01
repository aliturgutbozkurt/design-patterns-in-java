package io.github.aliturgutbozkurt.patterns.m01.examples.srp;

import io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.after.GradeReport;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.after.GradingScale;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.after.ScoreParser;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.before.GradeBook;

/**
 * Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/srp/GradeBookDemo.java}
 *
 * @see "m01 lesson, section SRP"
 */
public final class GradeBookDemo {

    private static final String CSV = """
            Ada,95,88,92
            Alan,72,65,80

            Grace,90,89.97,90
            Linus,40,55,50
            """;

    private GradeBookDemo() {}

    public static void main(String[] args) {
        System.out.println("== before: parse + grade + print in one class ==");
        String before = new GradeBook().report(CSV);
        System.out.print(before);

        System.out.println("== after: parser, grading scale, report ==");
        String after = new GradeReport(new GradingScale()).render(new ScoreParser().parse(CSV));
        System.out.print(after);

        System.out.println("same text? " + before.equals(after));
    }
}
