package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.logs;

import java.util.Objects;

/**
 * What travels through the queue: a log line, or the end of the stream. The poison pill is a <em>type</em>, not a
 * magic string such as {@code "EOF"} that a real log line could contain, and a {@code switch} over the sealed
 * interface cannot forget it.
 *
 * @see "m10 lesson, section Producer–Consumer"
 */
public sealed interface LogMessage {

    /** One line of the log file. */
    record LogLine(String text) implements LogMessage {
        public LogLine {
            Objects.requireNonNull(text, "text");
        }
    }

    /** The poison pill: "no more lines". Each consumer takes exactly one and stops. */
    record EndOfStream() implements LogMessage {}
}
