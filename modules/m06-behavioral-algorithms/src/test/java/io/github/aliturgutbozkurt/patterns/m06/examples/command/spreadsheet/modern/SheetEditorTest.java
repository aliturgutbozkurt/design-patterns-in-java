package io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.classic.Sheet;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern.SheetEdit.Batch;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern.SheetEdit.ClearCell;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern.SheetEdit.SetCell;
import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.util.List;
import org.junit.jupiter.api.Test;

class SheetEditorTest {

    private final Sheet sheet = new Sheet();
    private final SheetEditor editor = new SheetEditor(sheet);

    @Test
    void inverseOfSetOnAnEmptyCellIsClear() {
        assertThat(editor.apply(new SetCell("A1", "10"))).isEqualTo(new ClearCell("A1"));
    }

    @Test
    void inverseOfTheInverseIsTheOriginalEdit() {
        sheet.set("A1", "10");
        sheet.set("B1", "x");
        List<SheetEdit> edits = List.of(
                new SetCell("A1", "15"),
                new SetCell("Z9", "new"),
                new ClearCell("B1"),
                new Batch(List.of(new SetCell("C1", "1"), new SetCell("C2", "2"))));
        for (SheetEdit edit : edits) {
            SheetEdit inverse = editor.apply(edit);
            assertThat(editor.apply(inverse)).as("inverse of the inverse of %s", edit).isEqualTo(edit);
        }
    }

    @Test
    void undoingABatchRestoresAllItsCellsInOneStep() {
        editor.perform(new SetCell("A1", "10"));
        editor.perform(new Batch(List.of(new SetCell("B1", "20"), new SetCell("A1", "99"), new ClearCell("A1"))));
        assertThat(sheet.toString()).isEqualTo("{B1=20}");
        assertThat(editor.undo()).isTrue();
        assertThat(sheet.toString()).isEqualTo("{A1=10}");
    }

    @Test
    void undoOnEmptyHistoryReturnsFalse() {
        assertThat(editor.undo()).isFalse();
    }

    @Test
    void replayingTheLogOnAnEmptySheetRebuildsTheSameSheet() {
        editor.perform(new SetCell("A1", "10"));
        editor.perform(new Batch(List.of(new SetCell("B1", "20"), new ClearCell("A1"))));
        editor.perform(new SetCell("C3", "7"));
        editor.undo();
        assertThat(SheetEditor.replay(editor.log()).toString()).isEqualTo(sheet.toString());
    }

    @Test
    void editsAreValuesThatCanBeCompared() {
        assertThat(new Batch(List.of(new SetCell("A1", "1")))).isEqualTo(new Batch(List.of(new SetCell("A1", "1"))));
    }

    @Test
    void editHierarchyIsSealedSoTheSwitchNeedsNoDefault() {
        assertThat(SheetEdit.class.isSealed()).isTrue();
        assertThat(SheetEdit.class.getPermittedSubclasses())
                .containsExactlyInAnyOrder(SetCell.class, ClearCell.class, Batch.class);
    }

    @Test
    void demoPrintsEditsInversesAndReplay() {
        assertThat(Console.capture(() -> SpreadsheetDemo.main(new String[0]))).isEqualTo("""
                perform SetCell[cell=A1, value=10] -> {A1=10}
                perform Batch[edits=[SetCell[cell=B1, value=20], SetCell[cell=C1, value=30], ClearCell[cell=A1]]] -> {B1=20, C1=30}
                inverse Batch[edits=[SetCell[cell=A1, value=10], ClearCell[cell=C1], ClearCell[cell=B1]]]
                undo -> {A1=10}
                log has 3 edits; replayed on an empty sheet: {A1=10}
                """);
    }
}
