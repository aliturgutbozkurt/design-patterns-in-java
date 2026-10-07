package io.github.aliturgutbozkurt.patterns.capstone.api.pattern;

import static io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternCategory.ARCHITECTURAL;
import static io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternCategory.BEHAVIOURAL;
import static io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternCategory.CONCURRENCY;
import static io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternCategory.CREATIONAL;
import static io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternCategory.STRUCTURAL;

/**
 * GIVEN — do not modify. Every pattern taught in m02–m11, with its family.
 *
 * @see "capstone brief §3 — Pattern requirements"
 */
public enum DesignPattern {
    SINGLETON(CREATIONAL),
    STATIC_FACTORY_METHOD(CREATIONAL),
    FACTORY_METHOD(CREATIONAL),
    ABSTRACT_FACTORY(CREATIONAL),
    BUILDER(CREATIONAL),
    PROTOTYPE(CREATIONAL),
    OBJECT_POOL(CREATIONAL),

    ADAPTER(STRUCTURAL),
    BRIDGE(STRUCTURAL),
    COMPOSITE(STRUCTURAL),
    DECORATOR(STRUCTURAL),
    FACADE(STRUCTURAL),
    FLYWEIGHT(STRUCTURAL),
    PROXY(STRUCTURAL),

    CHAIN_OF_RESPONSIBILITY(BEHAVIOURAL),
    COMMAND(BEHAVIOURAL),
    INTERPRETER(BEHAVIOURAL),
    ITERATOR(BEHAVIOURAL),
    MEDIATOR(BEHAVIOURAL),
    MEMENTO(BEHAVIOURAL),
    OBSERVER(BEHAVIOURAL),
    STATE(BEHAVIOURAL),
    STRATEGY(BEHAVIOURAL),
    TEMPLATE_METHOD(BEHAVIOURAL),
    VISITOR(BEHAVIOURAL),

    THREAD_PER_TASK(CONCURRENCY),
    PRODUCER_CONSUMER(CONCURRENCY),
    GUARDED_SUSPENSION(CONCURRENCY),
    BALKING(CONCURRENCY),
    IMMUTABLE_OBJECT(CONCURRENCY),
    STRUCTURED_CONCURRENCY(CONCURRENCY),
    SCOPED_VALUE(CONCURRENCY),

    DEPENDENCY_INJECTION(ARCHITECTURAL),
    REPOSITORY(ARCHITECTURAL),
    SPECIFICATION(ARCHITECTURAL),
    PORTS_AND_ADAPTERS(ARCHITECTURAL),
    DOMAIN_EVENTS(ARCHITECTURAL);

    private final PatternCategory category;

    DesignPattern(PatternCategory category) {
        this.category = category;
    }

    /** The family this pattern belongs to. */
    public PatternCategory category() {
        return category;
    }
}
