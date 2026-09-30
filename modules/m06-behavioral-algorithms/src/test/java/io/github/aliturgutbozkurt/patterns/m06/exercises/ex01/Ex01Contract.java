package io.github.aliturgutbozkurt.patterns.m06.exercises.ex01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Assignment 01 — text editor with Command-based undo/redo. Each test is one acceptance criterion of the brief. */
public abstract class Ex01Contract {

    protected abstract Editor newEditor(String initialText, int maxHistory);

    private Editor editor(String text) {
        return newEditor(text, 100);
    }

    @Test
    void appliesInsertDeleteAndReplace() {
        Editor editor = editor("hello");
        editor.apply(new Insert(5, " world"));
        assertThat(editor.text()).isEqualTo("hello world");
        editor.apply(new Insert(0, ">"));
        assertThat(editor.text()).isEqualTo(">hello world");
        editor.apply(new Delete(0, 7));
        assertThat(editor.text()).isEqualTo("world");
        editor.apply(new Replace(0, 1, "W"));
        assertThat(editor.text()).isEqualTo("World");
    }

    @Test
    void undoRestoresPreviousText() {
        Editor editor = editor("abc");
        editor.apply(new Insert(3, "def"));
        editor.apply(new Insert(0, "_"));
        assertThat(editor.undo()).isTrue();
        assertThat(editor.text()).isEqualTo("abcdef");
        assertThat(editor.undo()).isTrue();
        assertThat(editor.text()).isEqualTo("abc");
    }

    @Test
    void undoRestoresExactDeletedText() {
        Editor editor = editor("abcdef");
        editor.apply(new Delete(1, 3));
        assertThat(editor.text()).isEqualTo("aef");
        editor.undo();
        assertThat(editor.text()).isEqualTo("abcdef");
        editor.apply(new Replace(2, 3, "XY"));
        assertThat(editor.text()).isEqualTo("abXYf");
        editor.undo();
        assertThat(editor.text()).isEqualTo("abcdef");
    }

    @Test
    void redoReappliesUndoneEdit() {
        Editor editor = editor("abc");
        editor.apply(new Replace(0, 1, "A"));
        editor.apply(new Delete(2, 1));
        editor.undo();
        editor.undo();
        assertThat(editor.canRedo()).isTrue();
        assertThat(editor.redo()).isTrue();
        assertThat(editor.text()).isEqualTo("Abc");
        assertThat(editor.redo()).isTrue();
        assertThat(editor.text()).isEqualTo("Ab");
        assertThat(editor.canRedo()).isFalse();
    }

    @Test
    void newEditClearsRedo() {
        Editor editor = editor("abc");
        editor.apply(new Insert(3, "d"));
        editor.undo();
        editor.apply(new Insert(0, "z"));
        assertThat(editor.canRedo()).isFalse();
        assertThat(editor.redo()).isFalse();
        assertThat(editor.text()).isEqualTo("zabc");
    }

    @Test
    void undoAndRedoOnEmptyHistoryReturnFalse() {
        Editor editor = editor("abc");
        assertThat(editor.canUndo()).isFalse();
        assertThat(editor.canRedo()).isFalse();
        assertThat(editor.undo()).isFalse();
        assertThat(editor.redo()).isFalse();
        assertThat(editor.text()).isEqualTo("abc");
    }

    @Test
    void invalidEditThrowsAndLeavesTextUnchanged() {
        Editor editor = editor("abc");
        List<Edit> invalid = List.of(
                new Insert(-1, "x"), new Insert(4, "x"),
                new Delete(-1, 1), new Delete(1, 3), new Delete(0, -1),
                new Replace(3, 1, "x"), new Replace(0, 4, "x"), new Replace(1, -1, "x"));
        for (Edit edit : invalid) {
            assertThatThrownBy(() -> editor.apply(edit)).as(edit.toString())
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
        assertThat(editor.text()).isEqualTo("abc");
        assertThat(editor.canUndo()).isFalse();
    }

    @Test
    void groupUndoesAsOneStep() {
        Editor editor = editor("name");
        editor.apply(new Insert(0, "my "));
        editor.group(e -> {
            e.apply(new Replace(0, 2, "your"));
            e.apply(new Insert(9, "!"));
        });
        assertThat(editor.text()).isEqualTo("your name!");
        editor.undo();
        assertThat(editor.text()).isEqualTo("my name");
        editor.redo();
        assertThat(editor.text()).isEqualTo("your name!");
        editor.undo();
        editor.undo();
        assertThat(editor.text()).isEqualTo("name");
        assertThat(editor.canUndo()).isFalse();
    }

    @Test
    void nestedGroupsJoinTheOuterGroup() {
        Editor editor = editor("");
        editor.group(outer -> {
            outer.apply(new Insert(0, "a"));
            outer.group(inner -> {
                inner.apply(new Insert(1, "b"));
                inner.apply(new Insert(2, "c"));
            });
            outer.apply(new Insert(3, "d"));
        });
        assertThat(editor.text()).isEqualTo("abcd");
        assertThat(editor.undo()).isTrue();
        assertThat(editor.text()).isEmpty();
        assertThat(editor.canUndo()).isFalse();
    }

    @Test
    void failedGroupIsRolledBackAndRethrown() {
        Editor editor = editor("keep");
        editor.apply(new Insert(4, "!"));
        var boom = new IllegalStateException("boom");
        assertThatThrownBy(() -> editor.group(e -> {
            e.apply(new Insert(0, "1"));
            e.apply(new Delete(0, 3));
            throw boom;
        })).isSameAs(boom);
        assertThat(editor.text()).isEqualTo("keep!");

        assertThatThrownBy(() -> editor.group(e -> {
            e.apply(new Insert(0, "x"));
            e.apply(new Delete(0, 99));  // invalid: throws inside the group
        })).isInstanceOf(IndexOutOfBoundsException.class);
        assertThat(editor.text()).isEqualTo("keep!");

        assertThat(editor.undo()).isTrue();  // only the edit made before the groups is left
        assertThat(editor.text()).isEqualTo("keep");
        assertThat(editor.canUndo()).isFalse();
    }

    @Test
    void historyIsBoundedToMaxSteps() {
        Editor editor = newEditor("", 2);
        editor.apply(new Insert(0, "a"));
        editor.apply(new Insert(1, "b"));
        editor.apply(new Insert(2, "c"));
        assertThat(editor.undo()).isTrue();
        assertThat(editor.undo()).isTrue();
        assertThat(editor.undo()).isFalse();  // the oldest step was dropped
        assertThat(editor.text()).isEqualTo("a");
        assertThatIllegalArgumentException().isThrownBy(() -> newEditor("", 0));
    }

    @Test
    void undoHistoryListsMostRecentFirst() {
        Editor editor = editor("abc");
        Edit first = new Insert(0, "x");
        Edit second = new Delete(1, 1);
        Edit third = new Replace(0, 1, "y");
        editor.apply(first);
        editor.apply(second);
        editor.apply(third);
        assertThat(editor.undoHistory()).containsExactly(third, second, first);
        editor.undo();
        assertThat(editor.undoHistory()).containsExactly(second, first);
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> newEditor(null, 10));
        Editor editor = editor("abc");
        assertThatNullPointerException().isThrownBy(() -> editor.apply(null));
        assertThatNullPointerException().isThrownBy(() -> editor.group(null));
        assertThat(editor.text()).isEqualTo("abc");
    }
}
