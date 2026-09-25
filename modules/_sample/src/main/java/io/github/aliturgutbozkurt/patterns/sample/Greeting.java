package io.github.aliturgutbozkurt.patterns.sample;

import java.util.Objects;

/**
 * Build-skeleton sample (F01): a bilingual greeting value.
 *
 * @param name     who to greet, not blank
 * @param language the greeting language
 */
public record Greeting(String name, Language language) {

    public Greeting {
        Objects.requireNonNull(language, "language");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }

    /** Returns the greeting text in the chosen language. */
    public String text() {
        return switch (language) {
            case EN -> "Hello, " + name + "!";
            case TR -> "Merhaba, " + name + "!";
        };
    }
}
