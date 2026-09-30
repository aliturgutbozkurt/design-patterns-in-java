package io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary;

import java.util.Objects;

/**
 * One CSV row after the boundary: either a valid {@link OrderLine} or the reason it was rejected.
 *
 * @see "m09 lesson, section Data-oriented programming — parse, don't validate"
 */
public sealed interface ImportedRow permits ImportedRow.Accepted, ImportedRow.Rejected {

    int lineNo();

    record Accepted(int lineNo, OrderLine line) implements ImportedRow {
        public Accepted {
            Objects.requireNonNull(line, "line");
        }
    }

    record Rejected(int lineNo, String reason) implements ImportedRow {
        public Rejected {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
