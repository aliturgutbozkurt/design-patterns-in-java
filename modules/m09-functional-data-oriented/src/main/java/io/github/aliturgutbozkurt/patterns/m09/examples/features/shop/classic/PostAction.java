package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

import java.util.List;

/**
 * Command: something to do after an invoice was produced.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public interface PostAction {

    void execute(List<String> log);
}
