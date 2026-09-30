package io.github.aliturgutbozkurt.patterns.m01.examples.ocp.sorting.before;

import io.github.aliturgutbozkurt.patterns.m01.examples.ocp.sorting.Course;
import java.util.ArrayList;
import java.util.List;

/**
 * OCP violation: the orderings are hard-coded; a new one means another {@code case} in this class.
 *
 * @see "m01 lesson, section OCP"
 */
public final class CourseSorter {

    /** Sorts by {@code "code"}, {@code "credits"} (highest first, then code) or {@code "title"}. */
    public List<Course> sort(List<Course> courses, String key) {
        List<Course> copy = new ArrayList<>(courses);
        switch (key) {
            case "code" -> copy.sort((a, b) -> a.code().compareTo(b.code()));
            case "credits" -> copy.sort((a, b) -> a.credits() != b.credits()
                    ? Integer.compare(b.credits(), a.credits())
                    : a.code().compareTo(b.code()));
            case "title" -> copy.sort((a, b) -> a.title().compareTo(b.title()));
            default -> throw new IllegalArgumentException("unknown sort key: " + key);
        }
        return List.copyOf(copy);
    }
}
