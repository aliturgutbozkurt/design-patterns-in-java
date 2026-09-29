package io.github.aliturgutbozkurt.patterns.m03.exercises.ex02;

import java.util.List;

/** Assignment 02 — your prototype registry. */
public class TemplateRegistry implements ShapeRegistry {

    @Override
    public void register(String name, Shape template) {
        // TODO(ex02): store a copy, so later changes to template do not leak in.
        throw new UnsupportedOperationException("TODO(ex02): implement register(String, Shape)");
    }

    @Override
    public Shape create(String name) {
        throw new UnsupportedOperationException("TODO(ex02): implement create(String)");
    }

    @Override
    public List<String> names() {
        throw new UnsupportedOperationException("TODO(ex02): implement names()");
    }
}
