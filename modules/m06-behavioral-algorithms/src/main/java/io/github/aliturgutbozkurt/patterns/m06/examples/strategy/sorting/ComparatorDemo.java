package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.sorting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/strategy/sorting/ComparatorDemo.java} */
public final class ComparatorDemo {

    private ComparatorDemo() {}

    public static void main(String[] args) {
        var roster = new ArrayList<>(List.of(
                new Student("Deniz", 3.2, 2, "Turing"),
                new Student("Ece", 3.5, 3, null),
                new Student("Ada", 3.9, 2, "Turing"),
                new Student("Cem", 3.9, 3, "Hopper"),
                new Student("Bora", 3.5, 1, null)));

        roster.sort(StudentOrderings.BY_GPA_DESC_THEN_NAME);
        print("by GPA desc, then name: ", roster,
                s -> s.name() + " " + String.format(Locale.ROOT, "%.1f", s.gpa()));

        roster.sort(StudentOrderings.BY_ADVISOR_NULLS_LAST_THEN_NAME);
        print("by advisor, none last:  ", roster,
                s -> s.name() + " (" + (s.advisor() == null ? "-" : s.advisor()) + ")");

        roster.sort(StudentOrderings.BY_NAME);
        roster.sort(StudentOrderings.BY_YEAR);  // stable: students in the same year stay in name order
        print("by name, then by year:  ", roster, s -> s.name() + " y" + s.year());
    }

    private static void print(String title, List<Student> students, Function<Student, String> format) {
        System.out.println(title + " " + students.stream().map(format).collect(Collectors.joining(", ")));
    }
}
