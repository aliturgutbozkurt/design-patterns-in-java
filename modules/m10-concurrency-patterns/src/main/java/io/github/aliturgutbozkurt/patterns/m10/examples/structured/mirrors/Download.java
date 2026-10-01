package io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors;

/**
 * A downloaded file and the mirror that delivered it.
 *
 * @see "m10 lesson, section Structured Concurrency"
 */
public record Download(String mirror, String content) {}
