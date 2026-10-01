package io.github.aliturgutbozkurt.patterns.m11.examples.archcheck;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * "Classes in {@code packagePrefix} must not depend on anything in {@code forbiddenPrefixes}." Checked against an
 * explicit list of classes — no classpath scanning, so it works under the source launcher too.
 *
 * @param packagePrefix the layer the rule is about
 * @param forbiddenPrefixes packages that layer must not use
 * @see "m11 lesson, section Architecture rules — how the tools work"
 */
public record LayerRule(String packagePrefix, Set<String> forbiddenPrefixes) {

    public LayerRule {
        Objects.requireNonNull(packagePrefix, "packagePrefix");
        forbiddenPrefixes = Set.copyOf(forbiddenPrefixes);
    }

    /** Every forbidden dependency among {@code classes}, sorted. */
    public List<Violation> check(DependencyScanner scanner, List<Class<?>> classes) {
        return classes.stream()
                .filter(type -> type.getName().startsWith(packagePrefix + "."))
                .flatMap(type -> scanner.dependenciesOf(type).stream()
                        .filter(dependency -> forbiddenPrefixes.stream()
                                .anyMatch(forbidden -> dependency.startsWith(forbidden + ".")))
                        .map(dependency -> new Violation(type.getName(), dependency)))
                .sorted()
                .toList();
    }
}
