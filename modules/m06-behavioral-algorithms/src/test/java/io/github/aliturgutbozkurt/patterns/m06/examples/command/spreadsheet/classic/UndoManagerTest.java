package io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.classic;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import org.junit.jupiter.api.Test;

class UndoManagerTest {

    private final Sheet sheet = new Sheet();
    private final UndoManager history = new UndoManager();

    @Test
    void undoRestoresThePreviousValue() {
        history.execute(new SetCellCommand(sheet, "A1", "10"));
        history.execute(new SetCellCommand(sheet, "A1", "15"));
        assertThat(history.undo()).isTrue();
        assertThat(sheet.get("A1")).contains("10");
    }

    @Test
    void undoRestoresAnEmptyCell() {
        history.execute(new SetCellCommand(sheet, "A1", "10"));
        history.undo();
        assertThat(sheet.get("A1")).isEmpty();
        assertThat(sheet.toString()).isEqualTo("{}");
    }

    @Test
    void undoOfClearPutsTheValueBack() {
        sheet.set("B2", "x");
        history.execute(new ClearCellCommand(sheet, "B2"));
        assertThat(sheet.get("B2")).isEmpty();
        history.undo();
        assertThat(sheet.get("B2")).contains("x");
    }

    @Test
    void redoReappliesTheUndoneCommand() {
        history.execute(new SetCellCommand(sheet, "A1", "10"));
        history.undo();
        assertThat(history.redo()).isTrue();
        assertThat(sheet.get("A1")).contains("10");
    }

    @Test
    void aNewCommandClearsTheRedoStack() {
        history.execute(new SetCellCommand(sheet, "A1", "10"));
        history.undo();
        history.execute(new SetCellCommand(sheet, "B1", "20"));
        assertThat(history.canRedo()).isFalse();
        assertThat(history.redo()).isFalse();
        assertThat(sheet.get("A1")).isEmpty();
    }

    @Test
    void undoAndRedoOnEmptyHistoryReturnFalse() {
        assertThat(history.undo()).isFalse();
        assertThat(history.redo()).isFalse();
    }

    @Test
    void undoLabelsListNewestFirst() {
        history.execute(new SetCellCommand(sheet, "A1", "10"));
        history.execute(new SetCellCommand(sheet, "B1", "20"));
        history.execute(new ClearCellCommand(sheet, "A1"));
        assertThat(history.undoLabels()).containsExactly("clear A1", "set B1=20", "set A1=10");
        history.undo();
        assertThat(history.redoLabels()).containsExactly("clear A1");
    }

    @Test
    void demoPrintsTheSheetAfterEveryStep() {
        assertThat(Console.capture(() -> SpreadsheetDemo.main(new String[0]))).isEqualTo("""
                set A1=10    {A1=10}
                set B1=20    {A1=10, B1=20}
                set A1=15    {A1=15, B1=20}
                undo stack   [set A1=15, set B1=20, set A1=10]
                undo         {A1=10, B1=20}
                undo         {A1=10}
                redo         {A1=10, B1=20}
                clear A1     {B1=20}
                can redo?    false
                undo         {A1=10, B1=20}
                """);
    }
}
