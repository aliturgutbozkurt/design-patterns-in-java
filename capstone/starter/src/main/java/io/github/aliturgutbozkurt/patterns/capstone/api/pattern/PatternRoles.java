package io.github.aliturgutbozkurt.patterns.capstone.api.pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * GIVEN — do not modify. Container of repeated {@link PatternRole}s (written by the compiler, not by you).
 *
 * @see "capstone brief, Pattern requirements"
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PatternRoles {

    /** The roles. */
    PatternRole[] value();
}
