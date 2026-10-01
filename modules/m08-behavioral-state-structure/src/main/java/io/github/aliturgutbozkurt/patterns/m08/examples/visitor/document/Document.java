package io.github.aliturgutbozkurt.patterns.m08.examples.visitor.document;

import java.util.List;

/**
 * A document: an ordered list of blocks.
 *
 * @see "m08 lesson, section Visitor — second example"
 */
public record Document(List<Block> blocks) {

    public Document {
        blocks = List.copyOf(blocks);
    }
}
