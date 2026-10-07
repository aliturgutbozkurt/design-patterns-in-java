package io.github.aliturgutbozkurt.patterns.capstone.api.pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * GIVEN — do not modify. Marks a type as a participant of a design pattern. The pattern inventory counts patterns from
 * it and the graders use it to find your code; repeat it when a type plays several roles.
 *
 * <pre>{@code
 * @PatternRole(value = DesignPattern.STRATEGY, role = "concrete strategy")
 * record CategoryPercentOffRule(Category category, int percent) implements PromotionRule { … }
 * }</pre>
 *
 * @see "capstone brief, Pattern requirements"
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(PatternRoles.class)
public @interface PatternRole {

    /** The pattern. */
    DesignPattern value();

    /** The role this type plays in it, in the words of the module lesson (e.g. {@code "concrete strategy"}). */
    String role();
}
