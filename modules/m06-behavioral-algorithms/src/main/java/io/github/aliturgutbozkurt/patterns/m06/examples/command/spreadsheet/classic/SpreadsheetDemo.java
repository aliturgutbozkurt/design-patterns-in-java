package io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.classic;

/**
 * Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/command/spreadsheet/classic/SpreadsheetDemo.java}
 *
 * @see "m06 lesson, section Command"
 */
public final class SpreadsheetDemo {

    private SpreadsheetDemo() {}

    public static void main(String[] args) {
        var sheet = new Sheet();
        var history = new UndoManager();

        run(history, new SetCellCommand(sheet, "A1", "10"), sheet);
        run(history, new SetCellCommand(sheet, "B1", "20"), sheet);
        run(history, new SetCellCommand(sheet, "A1", "15"), sheet);
        print("undo stack", history.undoLabels());

        history.undo();
        print("undo", sheet);
        history.undo();
        print("undo", sheet);
        history.redo();
        print("redo", sheet);

        run(history, new ClearCellCommand(sheet, "A1"), sheet);  // a new command empties the redo stack
        print("can redo?", history.canRedo());
        history.undo();
        print("undo", sheet);
    }

    private static void run(UndoManager history, Command command, Sheet sheet) {
        history.execute(command);
        print(command.label(), sheet);
    }

    private static void print(String step, Object state) {
        System.out.println(String.format("%-12s %s", step, state));
    }
}
