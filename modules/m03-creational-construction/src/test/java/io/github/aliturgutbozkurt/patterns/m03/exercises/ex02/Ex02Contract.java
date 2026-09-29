package io.github.aliturgutbozkurt.patterns.m03.exercises.ex02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Assignment 02 — shape editor with prototypes. Each test is one acceptance criterion of the brief. */
public abstract class Ex02Contract {

    protected abstract Shape circle(Point centre, int radius);

    protected abstract Shape rect(Point corner, int width, int height);

    protected abstract Shape group(List<Shape> children);

    protected abstract ShapeRegistry registry();

    private Shape sampleGroup() {
        return group(List.of(circle(new Point(1, 2), 5), rect(new Point(0, 0), 3, 4)));
    }

    @Test
    void copyDescribesTheSameShape() {
        assertThat(circle(new Point(1, 2), 5).copy().describe()).isEqualTo("circle r=5 at (1,2)");
        assertThat(rect(new Point(0, 0), 3, 4).copy().describe()).isEqualTo("rect 3x4 at (0,0)");
        assertThat(sampleGroup().copy().describe()).isEqualTo("group[circle r=5 at (1,2), rect 3x4 at (0,0)]");
    }

    @Test
    void copyIsANewObject() {
        Shape original = sampleGroup();
        assertThat(original.copy()).isNotSameAs(original);
    }

    @Test
    void movingACopyLeavesTheOriginal() {
        Shape original = circle(new Point(1, 2), 5);
        Shape copy = original.copy();
        copy.moveBy(10, 10);
        assertThat(original.position()).isEqualTo(new Point(1, 2));
        assertThat(copy.position()).isEqualTo(new Point(11, 12));
    }

    @Test
    void groupCopyIsDeep() {
        Shape original = sampleGroup();
        Shape copy = original.copy();
        copy.moveBy(10, 0);
        assertThat(copy.describe()).isEqualTo("group[circle r=5 at (11,2), rect 3x4 at (10,0)]");
        assertThat(original.describe()).isEqualTo("group[circle r=5 at (1,2), rect 3x4 at (0,0)]");
    }

    @Test
    void nestedGroupsAreCopiedDeeply() {
        Shape original = group(List.of(group(List.of(circle(new Point(0, 0), 1))), rect(new Point(5, 5), 1, 1)));
        Shape copy = original.copy();
        copy.moveBy(1, 1);
        assertThat(original.describe()).isEqualTo("group[group[circle r=1 at (0,0)], rect 1x1 at (5,5)]");
        assertThat(copy.describe()).isEqualTo("group[group[circle r=1 at (1,1)], rect 1x1 at (6,6)]");
    }

    @Test
    void registryReturnsFreshCopies() {
        ShapeRegistry registry = registry();
        registry.register("logo", sampleGroup());
        Shape first = registry.create("logo");
        Shape second = registry.create("logo");
        assertThat(first).isNotSameAs(second);
        assertThat(first.describe()).isEqualTo(second.describe());
    }

    @Test
    void registeredTemplateIsNotAffectedByChangesToCreatedShapes() {
        ShapeRegistry registry = registry();
        registry.register("dot", circle(new Point(0, 0), 1));
        registry.create("dot").moveBy(7, 7);
        assertThat(registry.create("dot").position()).isEqualTo(new Point(0, 0));
    }

    @Test
    void registeringCopiesTheTemplate() {
        ShapeRegistry registry = registry();
        Shape template = rect(new Point(0, 0), 2, 2);
        registry.register("box", template);
        template.moveBy(3, 3);
        assertThat(registry.create("box").position()).isEqualTo(new Point(0, 0));
    }

    @Test
    void unknownTemplateRejected() {
        assertThatIllegalArgumentException().isThrownBy(() -> registry().create("star"));
    }

    @Test
    void namesAreSorted() {
        ShapeRegistry registry = registry();
        registry.register("zebra", circle(new Point(0, 0), 1));
        registry.register("apple", circle(new Point(0, 0), 1));
        registry.register("mango", circle(new Point(0, 0), 1));
        assertThat(registry.names()).containsExactly("apple", "mango", "zebra");
    }
}
