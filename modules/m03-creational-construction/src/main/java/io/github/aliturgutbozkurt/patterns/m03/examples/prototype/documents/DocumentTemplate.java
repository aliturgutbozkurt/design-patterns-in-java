package io.github.aliturgutbozkurt.patterns.m03.examples.prototype.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A mutable document template with two ways to copy it: {@link #clone()} (shallow — the classic trap) and the copy
 * constructor {@link #DocumentTemplate(DocumentTemplate)} (deep). Because the collection fields are {@code final},
 * {@code clone()} <em>cannot</em> replace them with copies — one of the reasons {@code Cloneable} is considered broken.
 *
 * @see "m03 lesson, section Prototype"
 */
public final class DocumentTemplate implements Cloneable {

    private String title;
    private final List<String> sections;
    private final Map<String, String> metadata;

    public DocumentTemplate(String title, List<String> sections, Map<String, String> metadata) {
        this.title = Objects.requireNonNull(title, "title");
        this.sections = new ArrayList<>(sections);
        this.metadata = new LinkedHashMap<>(metadata);
    }

    /** Copy constructor: a deep copy — new lists and maps with the same (immutable) strings. */
    public DocumentTemplate(DocumentTemplate other) {
        this(other.title, other.sections, other.metadata);
    }

    /** Shallow copy: the clone shares {@code sections} and {@code metadata} with this template. */
    @Override
    public DocumentTemplate clone() {
        try {
            return (DocumentTemplate) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloneable is implemented", e);
        }
    }

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "title");
    }

    public List<String> sections() {
        return Collections.unmodifiableList(sections);
    }

    public void addSection(String section) {
        sections.add(Objects.requireNonNull(section, "section"));
    }

    public Map<String, String> metadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public void putMetadata(String key, String value) {
        metadata.put(key, value);
    }

    @Override
    public String toString() {
        return title + " " + sections + " " + metadata;
    }
}
