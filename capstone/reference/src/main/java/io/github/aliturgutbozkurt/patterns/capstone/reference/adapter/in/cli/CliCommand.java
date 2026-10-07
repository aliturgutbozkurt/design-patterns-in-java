package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.in.cli;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;

/**
 * One CLI command as an object in the adapter's command table: parses its arguments, calls a use case, formats the
 * response. A {@link UsageException} means "show my usage line".
 *
 * @see "capstone guide §1 Pattern map — Command"
 */
@PatternRole(value = DesignPattern.COMMAND, role = "command (entries of the CLI command table)")
@FunctionalInterface
interface CliCommand {

    String run(CliArgs args);
}
