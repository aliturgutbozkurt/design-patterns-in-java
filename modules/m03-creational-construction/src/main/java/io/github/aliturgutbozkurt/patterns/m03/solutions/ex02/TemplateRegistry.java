package io.github.aliturgutbozkurt.patterns.m03.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.Shape;
import io.github.aliturgutbozkurt.patterns.m03.exercises.ex02.ShapeRegistry;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Reference solution for assignment 02: copies in, copies out. */
public class TemplateRegistry implements ShapeRegistry {

    private final Map<String, Shape> templates = new TreeMap<>();

    @Override
    public void register(String name, Shape template) {
        templates.put(name, template.copy());
    }

    @Override
    public Shape create(String name) {
        Shape template = templates.get(name);
        if (template == null) {
            throw new IllegalArgumentException("unknown template: " + name + " (known: " + names() + ")");
        }
        return template.copy();
    }

    @Override
    public List<String> names() {
        return List.copyOf(templates.keySet());
    }
}
