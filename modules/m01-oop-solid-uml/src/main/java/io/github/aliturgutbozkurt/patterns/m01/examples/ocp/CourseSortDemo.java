package io.github.aliturgutbozkurt.patterns.m01.examples.ocp;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.sorting.Course;
import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.sorting.after.CourseCatalog;
import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.sorting.before.CourseSorter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/** Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/ocp/CourseSortDemo.java} */
public final class CourseSortDemo {

    private CourseSortDemo() {}

    public static void main(String[] args) {
        List<Course> courses = List.of(
                new Course("CENG213", "Data Structures", 7),
                new Course("MATH119", "Calculus", 7),
                new Course("CENG101", "Programming I", 6),
                new Course("CENG315", "Algorithms", 8),
                new Course("CENG242", "Programming Languages", 6));

        System.out.println("== before: switch on a sort-key string ==");
        var sorter = new CourseSorter();
        for (String key : List.of("code", "credits", "title")) {
            System.out.println(key + ": " + codes(sorter.sort(courses, key)));
        }

        System.out.println("== after: orderings are Comparators ==");
        var catalog = new CourseCatalog(courses);
        var orderings = new LinkedHashMap<String, Comparator<Course>>();
        orderings.put("code", comparing(Course::code));
        orderings.put("credits", comparingInt(Course::credits).reversed().thenComparing(Course::code));
        orderings.put("title", comparing(Course::title));
        orderings.forEach((name, order) -> System.out.println(name + ": " + codes(catalog.sorted(order))));

        Comparator<Course> byDepartmentThenCredits = comparing(Course::department)
                .thenComparing(comparingInt(Course::credits).reversed())
                .thenComparing(Course::code);
        System.out.println("department, then credits: " + codes(catalog.sorted(byDepartmentThenCredits))
                + " (new ordering, catalog unchanged)");
    }

    private static String codes(List<Course> courses) {
        return courses.stream().map(Course::code).collect(Collectors.joining(", "));
    }
}
