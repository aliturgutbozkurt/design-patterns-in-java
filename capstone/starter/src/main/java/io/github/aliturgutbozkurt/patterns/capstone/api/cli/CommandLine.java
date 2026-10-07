package io.github.aliturgutbozkurt.patterns.capstone.api.cli;

/**
 * GIVEN — do not modify. Inbound port of feature F11: one command line in, the response out (no trailing newline;
 * several lines joined with {@code \n}). Never throws for bad input: unknown commands, usage errors and use-case
 * exceptions become {@code ERROR …} / {@code USAGE …} responses.
 *
 * @see "capstone brief, Business rules — CLI (F11); SPEC-capstone, Output formats"
 */
@FunctionalInterface
public interface CommandLine {

    /** Executes one command, e.g. {@code cart add cart-1 BOK-001 2}. */
    String execute(String line);
}
