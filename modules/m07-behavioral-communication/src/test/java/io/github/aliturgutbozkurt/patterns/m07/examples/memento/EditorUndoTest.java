package io.github.aliturgutbozkurt.patterns.m07.examples.memento;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m07.examples.memento.editor.Editor;
import io.github.aliturgutbozkurt.patterns.m07.examples.memento.editor.EditorSnapshot;
import io.github.aliturgutbozkurt.patterns.m07.examples.memento.editor.UndoHistory;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import org.junit.jupiter.api.Test;

class EditorUndoTest {

    private final Editor editor = new Editor(10);

    @Test
    void undoThenRedoRoundTrips() {
        editor.type("Hello");
        editor.select(0, 5);
        editor.type("Bye");
        EditorSnapshot after = editor.snapshot();
        assertThat(editor.undo()).isTrue();
        assertThat(editor.render()).isEqualTo("[Hello]");
        assertThat(editor.redo()).isTrue();
        assertThat(editor.snapshot()).isEqualTo(after);
    }

    @Test
    void newEditClearsTheRedoStack() {
        editor.type("a");
        editor.type("b");
        editor.undo();
        editor.type("c");
        assertThat(editor.redo()).isFalse();
        assertThat(editor.render()).isEqualTo("ac|");
    }

    @Test
    void capacityKeepsOnlyTheNewestSnapshots() {
        var small = new Editor(3);
        for (String letter : new String[] {"a", "b", "c", "d", "e"}) {
            small.type(letter);
        }
        assertThat(small.history()).extracting(EditorSnapshot::text).containsExactly("abcd", "abc", "ab");
        assertThat(small.undo()).isTrue();
        assertThat(small.undo()).isTrue();
        assertThat(small.undo()).isTrue();
        assertThat(small.undo()).isFalse();
        assertThat(small.render()).isEqualTo("ab|");
    }

    @Test
    void historyListsNewestFirst() {
        editor.type("x");
        editor.moveCursor(0);
        assertThat(editor.history()).containsExactly(
                new EditorSnapshot("x", 1, 1, 1), new EditorSnapshot("", 0, 0, 0));
    }

    @Test
    void undoAndRedoOnEmptyHistoryReturnFalseAndChangeNothing() {
        EditorSnapshot before = editor.snapshot();
        assertThat(editor.undo()).isFalse();
        assertThat(editor.redo()).isFalse();
        assertThat(editor.snapshot()).isEqualTo(before);
    }

    @Test
    void snapshotRejectsCursorOrSelectionOutsideTheText() {
        assertThatIllegalArgumentException().isThrownBy(() -> new EditorSnapshot("abc", 4, 0, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> new EditorSnapshot("abc", 1, 2, 1));
        assertThatIllegalArgumentException().isThrownBy(() -> new EditorSnapshot("abc", 1, 0, 4));
        assertThatIllegalArgumentException().isThrownBy(() -> editor.select(0, 1));
    }

    @Test
    void peekUndoShowsWhatTheNextUndoRestores() {
        var history = new UndoHistory(2);
        assertThat(history.peekUndo()).isEmpty();
        var snapshot = new EditorSnapshot("a", 1, 1, 1);
        history.record(snapshot);
        assertThat(history.peekUndo()).contains(snapshot);
    }

    @Test
    void demoPrintsEveryStepAndTheHistory() {
        assertThat(Console.capture(() -> EditorUndoDemo.main(new String[0]))).isEqualTo("""
                select -> Hello [world]
                type -> Hello Java|
                undo true -> Hello [world]
                undo true -> Hello world|
                redo true -> Hello [world]
                type -> Hello there|
                redo false -> Hello there|
                history (newest first, capacity 3): [Hello [world], Hello world|, Hello|]
                """);
    }
}
