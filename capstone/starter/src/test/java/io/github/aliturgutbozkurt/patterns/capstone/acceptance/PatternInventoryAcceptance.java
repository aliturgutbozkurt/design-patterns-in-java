package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternCategory;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * The pattern requirement of brief §3, as far as bytecode can show it: the {@code @PatternRole} declarations in the
 * application's production classes. Whether each pattern is real and justified is graded by the rubric (C2, C4).
 */
public abstract class PatternInventoryAcceptance extends AcceptanceContract {

    /** Every production class of the application, with its {@code @PatternRole}s. */
    private List<Class<?>> productionClasses() {
        return new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(applicationRootPackage()).stream()
                .map(JavaClass::reflect)
                .collect(Collectors.toList());
    }

    private List<PatternRole> roles() {
        return productionClasses().stream()
                .flatMap(type -> Arrays.stream(type.getAnnotationsByType(PatternRole.class)))
                .toList();
    }

    /** The declared patterns that count towards the ten (every family except ARCHITECTURAL). */
    private Set<DesignPattern> countedPatterns() {
        return roles().stream().map(PatternRole::value)
                .filter(pattern -> pattern.category() != PatternCategory.ARCHITECTURAL)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Test
    void declaresAtLeastTenDistinctPatterns() {
        assertThat(roles()).as("every @PatternRole names its role").allSatisfy(role ->
                assertThat(role.role()).as("role of %s", role.value()).isNotBlank());
        assertThat(countedPatterns()).as("distinct creational, structural, behavioural and concurrency patterns")
                .hasSizeGreaterThanOrEqualTo(10);
    }

    @Test
    void meetsTheMinimumMixPerCategory() {
        Map<PatternCategory, Long> perCategory = new EnumMap<>(PatternCategory.class);
        countedPatterns().stream()
                .filter(pattern -> pattern != DesignPattern.IMMUTABLE_OBJECT) // records alone are no concurrency design
                .forEach(pattern -> perCategory.merge(pattern.category(), 1L, Long::sum));

        assertThat(perCategory.getOrDefault(PatternCategory.CREATIONAL, 0L)).as("creational").isGreaterThanOrEqualTo(2);
        assertThat(perCategory.getOrDefault(PatternCategory.STRUCTURAL, 0L)).as("structural").isGreaterThanOrEqualTo(2);
        assertThat(perCategory.getOrDefault(PatternCategory.BEHAVIOURAL, 0L)).as("behavioural")
                .isGreaterThanOrEqualTo(3);
        assertThat(perCategory.getOrDefault(PatternCategory.CONCURRENCY, 0L)).as("concurrency, not Immutable Object")
                .isGreaterThanOrEqualTo(1);
    }

    @Test
    void coreDeclaresASealedHierarchyOfRecords() {
        List<String> sealedRecordHierarchies = productionClasses().stream()
                .filter(type -> type.isInterface() && type.isSealed())
                .filter(type -> type.getPermittedSubclasses().length >= 2
                        && Arrays.stream(type.getPermittedSubclasses()).allMatch(Class::isRecord))
                .map(Class::getName)
                .toList();

        assertThat(sealedRecordHierarchies).as("sealed interfaces whose permitted subclasses are all records (≥ 2)")
                .isNotEmpty();
    }
}
